package smartcity;

import com.example.taxis.GrcpGrpc;
import com.example.taxis.GrcpOuterClass;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import models.Coordinate;
import models.Ride;
import models.Taxi;
import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;
import java.util.Date;

public class TaxiSubPub extends Thread {

    private static MqttClient client;
    private int district;
    private String topic;

    private int count;
    private int countRecharge;

    public TaxiSubPub(int district) {
        this.district = district;
        this.topic = "seta/smartcity/rides/district" + district;
        //this.topic = "seta/smartcity/rides/district"+1;
    }

    public int getDistrict() {
        return this.district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void changeDistrict(int district) throws MqttException {
        client.unsubscribe(topic);
        System.out.println("Mi sono disiscritto dal TOPIC: " + topic);
        setDistrict(district);
        setTopic("seta/smartcity/rides/district" + district);
        client.subscribe(this.topic);
        System.out.println("Mi sono iscritto al nuovo TOPIC: " + topic);
    }

    @Override
    public void run() {

        super.run();

        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        int qos = 2;

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.connect(connOpts);
            client.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) {

                    String receivedMessage = new String(message.getPayload()); // da binario a stringa

                    int id = Integer.parseInt(receivedMessage.split(",")[0].split("=")[1]);
                    int x1 = Integer.parseInt(receivedMessage.split(",")[1].split("=")[1].split("--")[0]);
                    int y1 = Integer.parseInt(receivedMessage.split(",")[1].split("=")[1].split("--")[1]);
                    int x2 = Integer.parseInt(receivedMessage.split(",")[2].split("=")[1].split("--")[0]);
                    int y2 = Integer.parseInt(receivedMessage.split(",")[2].split("=")[1].split("--")[1].split("}")[0]);

                    Coordinate rit = new Coordinate(x1, y1);
                    Coordinate con = new Coordinate(x2, y2);
                    Ride ride = new Ride(id, rit, con);

                    // System.out.println("NUOVA CORSA DISTRETTO: " + ride.getStartPosition().getDistrict());

                    // start election algo

                    // se il taxi sta ricaricando o è già occupato non fa nulla
                    if (!TaxisSingleton.getInstance().isRiding() && !TaxisSingleton.getInstance().isRecharging()) {

                        TaxisSingleton.getInstance().setPartecipant(true); // Set taxi to partecipant
                        count = 0; // azzero il count delle risposte

                        ArrayList<Taxi> otherTaxiList = TaxisSingleton.getInstance().getTaxiList();

                        if (otherTaxiList.size() == 0) { // GESTISCO IO LA RICHIESTA, SONO DA SOLO

                            try {

                                System.out.println("GESTISCO IO LA RIDE: " + ride.getId());
                                TaxisSingleton.getInstance().setRiding(true);

                                // Notifico che non sono più in un'elezione con il notify
                                synchronized (TaxisSingleton.getInstance().getParticipantElectionLock()) {
                                    TaxisSingleton.getInstance().setPartecipant(false);
                                    TaxisSingleton.getInstance().getParticipantElectionLock().notify();
                                }

                                // AVVISO SETA CHE HO PRESO IN CARICA LA RICHIESTA
                                String payload = "Ride ID: " + ride.getId() + " managed by Taxi: " + TaxisSingleton.getInstance().getCurrentTaxi().getId();
                                MqttMessage messageRide = new MqttMessage(payload.getBytes());
                                messageRide.setQos(qos);
                                client.publish("seta/smartcity/rides/accomplished/" + ride.getId(), messageRide);

                                // MANAGED RIDE
                                takeRun(ride);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                            for (Taxi t : otherTaxiList) {
                                // System.out.println(t.getId());
                                System.out.println("Send election to Taxi: " + t.getId() + " for Ride: " + ride.getId());
                                final ManagedChannel channel = ManagedChannelBuilder
                                        .forTarget(t.getServerAddress() + ":" + t.getPort())
                                        .usePlaintext()
                                        .build();

                                double distance = getDistanceFromCoordinate(t.getCoordinate(), ride.getStartPosition());

                                GrcpGrpc.GrcpBlockingStub stub = GrcpGrpc.newBlockingStub(channel);

                                GrcpOuterClass.ElectionRequest req = GrcpOuterClass.ElectionRequest.newBuilder()
                                        .setTaxiId(TaxisSingleton.getInstance().getCurrentTaxi().getId())
                                        .setRideId(ride.getId())
                                        .setBattery(TaxisSingleton.getInstance().getCurrentTaxi().getBatteryLevel())
                                        .setDistrict(TaxisSingleton.getInstance().getCurrentTaxi()
                                                .getCoordinate().getDistrict())
                                        .setStartX(ride.getStartPosition().getX())
                                        .setStartY(ride.getStartPosition().getY())
                                        .setEndX(ride.getEndPosition().getX())
                                        .setEndY(ride.getEndPosition().getY())
                                        .setDistance(distance)
                                        .build();

                                GrcpOuterClass.ElectionResponse res;
                                try {
                                    res = stub.election(req);

                                    if (res.getResult().equals("OK")) {
                                        count++; // incremento del count ad ogni risposta
                                    } else {

                                        synchronized (TaxisSingleton.getInstance().getParticipantElectionLock()) {
                                            TaxisSingleton.getInstance().setPartecipant(false);
                                            TaxisSingleton.getInstance().getParticipantElectionLock().notify();
                                        }

                                        System.out.println("NON GESTISCO IO LA RIDE: " + ride.getId() + " nel district: " + ride.getStartPosition().getDistrict());

                                        break; // Non vincerò sicuramente io l'elezione, termino
                                    }
                                    //System.out.println(res);
                                    if (count == otherTaxiList.size()) {
                                        System.out.println("COUNT OK: " + count);
                                        System.out.println("DIMENSIONE LISTA: " + otherTaxiList.size());
                                        System.out.println("GESTISCO IO LA RIDE: " + ride.getId() + " nel district: " + ride.getStartPosition().getDistrict());
                                        TaxisSingleton.getInstance().setRiding(true);

                                        // Notifico che non sono più in un'elezione con il notify
                                        synchronized (TaxisSingleton.getInstance().getParticipantElectionLock()) {
                                            TaxisSingleton.getInstance().setPartecipant(false);
                                            TaxisSingleton.getInstance().getParticipantElectionLock().notify();
                                        }

                                        // AVVISO SETA CHE HO PRESO IN CARICA LA RICHIESTA
                                        String payload = "Ride ID: " + ride.getId() + " managed by Taxi: " + TaxisSingleton.getInstance().getCurrentTaxi().getId();
                                        MqttMessage messageRide = new MqttMessage(payload.getBytes());
                                        messageRide.setQos(qos);
                                        client.publish("seta/smartcity/rides/accomplished/" + ride.getId(), messageRide);

                                        // MANAGED RIDE
                                        takeRun(ride);
                                    }
                                } catch (Exception e) {
                                    TaxisSingleton.getInstance().removeTaxiById(t.getId());
                                    System.out.println("NON RIESCO A CONTATTARE IL TAXI: " + t.getId() + " LO ELIMINO");
                                }

                                channel.shutdownNow();
                            }
                        }
                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection Lost");
                    System.out.println(cause.toString());
                }
            });

            client.subscribe(topic, qos);
            System.out.println("Subscribed to topics : " + topic);


        } catch (MqttException e) {
            System.out.println("MqttException");
        }
    }

    public static void disconnect() {
        //System.out.println("disconnectClient()");
        try {
            client.disconnect();
        } catch (MqttException e) {
            System.out.println("disconnectClient - Errore: " + e);
        }
        System.out.println("MQTT Client disconnected!");
    }

    public double getDistanceFromCoordinate(Coordinate start, Coordinate end) {


        return Math.sqrt(
                Math.pow(end.getX() - start.getX(), 2) +
                        Math.pow(end.getY() - start.getY(), 2));
    }

    public void takeRun(Ride ride) throws InterruptedException, MqttException {

        System.out.println("Sto effettuando la corsa...");

        Thread.sleep(5000);

        /*
            1. 5 secondi per ride
            2. Cambio i miei dati
                2.1 1% di batteria per ogni chilometro
            3. RCP per inviare i miei nuovi dati a tutti gli altri taxi
            4. Mi iscrivo ad un altro topic, quello del mio nuovo distretto
            5. setRiding a false
            6. Notify lock
        */

        // ride info
        int km = (int) Math.round(ride.getDistance());
        System.out.println("Km percorsi: " + km);

        // set new data taxi
        int newBatteryLevel = TaxisSingleton.getInstance().getCurrentTaxi().getBatteryLevel() - 1 * km;
        Coordinate newCordinate = new Coordinate(ride.getEndPosition().getX(), ride.getEndPosition().getY());


        // TODO: CHECK BATTERIA
        /*
            BATTERIA
            1. Se sotto il 30%, algoritmo di mutua esclusione
            2. Mi metto in wait, finchè non mi ricarico non vado avanti
         */

        System.out.println("BATTERIA RESIDUA 🔋: " + newBatteryLevel);

        if (newBatteryLevel < 30) {

            TaxisSingleton.getInstance().setRecharging(1);

            Date date = new Date();
            long timestamp = date.getTime(); // timestamp in ms

            // chiamata GRPC
            ArrayList<Taxi> otherTaxiList = TaxisSingleton.getInstance().getTaxiList();
            countRecharge = 0;

            for (Taxi t : otherTaxiList) {

                final ManagedChannel channel = ManagedChannelBuilder
                        .forTarget(t.getServerAddress() + ":" + t.getPort())
                        .usePlaintext()
                        .build();

                GrcpGrpc.GrcpBlockingStub stub = GrcpGrpc.newBlockingStub(channel);

                GrcpOuterClass.RechargeTaxiRequest req = GrcpOuterClass.RechargeTaxiRequest.newBuilder()
                        .setTaxiId(TaxisSingleton.getInstance().getCurrentTaxi().getId())
                        .setDistrict(newCordinate.getDistrict())
                        .setTimestamp(timestamp)
                        .build();

                GrcpOuterClass.RechargeTaxiResponse res;

                try {
                    res = stub.recharge(req);
                    if (res.getReply().equals("OK")) {
                        countRecharge++;
                    }

                } catch (Exception e) {
                    TaxisSingleton.getInstance().removeTaxiById(t.getId());
                    System.out.println("NON RIESCO A CONTATTARE IL TAXI: " + t.getId() + " LO ELIMINO");
                }

                channel.shutdownNow();

            }

            // MI POSSO RICARICARE
            if (countRecharge == otherTaxiList.size()) ;
            {

                TaxisSingleton.getInstance().setRecharging(2); // la uso

                System.out.println("MI STO RICARICANDO");
                Thread.sleep(10000);
                System.out.println("RICARICA COMPLETATA");

                // Aggiorno livello batteria e posizione
                newBatteryLevel = 100;

                ArrayList<Integer> newCordinateArray = newCordinate.getStationCoordinateByDistrict();
                newCordinate = new Coordinate(newCordinateArray.get(0),newCordinateArray.get(1)); // prendo il distretto da dove sono partito per ricaricarmi

                // Non sto più usando la stazione di ricarica
                TaxisSingleton.getInstance().setRecharging(0);

                synchronized (TaxisSingleton.getInstance().getChargeBatteryLock()) {
                    TaxisSingleton.getInstance().getChargeBatteryLock().notifyAll();
                }
            }
        }

        TaxisSingleton.getInstance().getCurrentTaxi().setBatteryLevel(newBatteryLevel);
        TaxisSingleton.getInstance().getCurrentTaxi().setCoordinate(newCordinate);
        TaxisSingleton.getInstance().addKm(km); // aggiorno il numero di km percorsi
        TaxisSingleton.getInstance().addRide(); // aggiorno count delle ride


        // send my new info to all other taxi
        ArrayList<Taxi> otherTaxiList = TaxisSingleton.getInstance().getTaxiList();
        for (Taxi t : otherTaxiList) {
            final ManagedChannel channel = ManagedChannelBuilder
                    .forTarget(t.getServerAddress() + ":" + t.getPort())
                    .usePlaintext()
                    .build();

            GrcpGrpc.GrcpBlockingStub stub = GrcpGrpc.newBlockingStub(channel);

            GrcpOuterClass.UpdateTaxiInfoRequest req = GrcpOuterClass.UpdateTaxiInfoRequest.newBuilder()
                    .setBattery(newBatteryLevel)
                    .setX(newCordinate.getX())
                    .setY(newCordinate.getY())
                    .setId(TaxisSingleton.getInstance().getCurrentTaxi().getId())
                    .build();

            GrcpOuterClass.UpdateTaxiInfoResponse res;
            try {
                res = stub.updateTaxiInfo(req);
            } catch (Exception e) {
                TaxisSingleton.getInstance().removeTaxiById(t.getId());
                System.out.println("NON RIESCO A CONTATTARE IL TAXI: " + t.getId() + " LO ELIMINO");
            }
        }

        changeDistrict(ride.getEndPosition().getDistrict());

        // Avviso SETA che mi sono liberato
        String payload = "Taxi ID: " + TaxisSingleton.getInstance().getCurrentTaxi().getId() + " now is FREE ";
        MqttMessage messageRide = new MqttMessage(payload.getBytes());
        messageRide.setQos(2);

        client.publish("seta/smartcity/taxis/free/" + TaxisSingleton.getInstance().getCurrentTaxi().getId(), messageRide);

        System.out.println("Ho finito la corsa...");


        // Notifico che mi sono liberato con il notify
        synchronized (TaxisSingleton.getInstance().getDeliveryInProgressLock()) {
            TaxisSingleton.getInstance().setRiding(false);
            TaxisSingleton.getInstance().getDeliveryInProgressLock().notify();
        }

    }

}
