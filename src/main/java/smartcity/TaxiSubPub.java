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

public class TaxiSubPub extends Thread{

    private static MqttClient client;
    private int district;
    private String topic;

    private int count;

    public TaxiSubPub(int district){
        this.district = district;
        this.topic = "seta/smartcity/rides/district"+district;
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

    public void changeDistrict (int district) throws MqttException {
        client.unsubscribe(topic);
        System.out.println("Mi sono disiscritto dal TOPIC: " + topic);
        setDistrict(district);
        setTopic("seta/smartcity/rides/district"+district);
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

                        System.out.println("NUOVA CORSA DISTRETTO: " + ride.getStartPosition().getDistrict());

                        // start election algo

                        // se il taxi sta ricaricando o è già occupato non fa nulla
                        if (!TaxisSingleton.getInstance().getCurrentTaxi().isRiding() && !TaxisSingleton.getInstance().getCurrentTaxi().isRecharging()){

                            TaxisSingleton.getInstance().getCurrentTaxi().setPartecipant(true); // Set taxi to partecipant
                            count = 0; // azzero il count delle risposte

                            ArrayList<Taxi> otherTaxiList = TaxisSingleton.getInstance().getTaxiList();

                            if(otherTaxiList.size() == 0){ // GESTISCO IO LA RICHIESTA, SONO DA SOLO

                                try {

                                    System.out.println("GESTISCO IO LA RIDE: " + ride.getId());
                                    TaxisSingleton.getInstance().getCurrentTaxi().setRiding(true);

                                    // AVVISO SETA CHE HO PRESO IN CARICA LA RICHIESTA

                                    String payload = "Ride ID: " + ride.getId() + " managed by Taxi: " +  TaxisSingleton.getInstance().getCurrentTaxi().getId();
                                    MqttMessage messageRide = new MqttMessage(payload.getBytes());


                                    messageRide.setQos(qos);
                                    client.publish("seta/smartcity/rides/accomplished/"+ride.getId(),messageRide);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {

                                for (Taxi t : otherTaxiList) {
                                    // System.out.println(t.getId());
                                    System.out.println("Send election to Taxi: " + t.getId() + " for Ride: " + ride.getId());
                                    final ManagedChannel channel = ManagedChannelBuilder
                                            .forTarget(t.getServerAddress() + ":" + t.getPort())
                                            .usePlaintext()
                                            .build();

                                    GrcpGrpc.GrcpBlockingStub stub = GrcpGrpc.newBlockingStub(channel);

                                    GrcpOuterClass.ElectionRequest req = GrcpOuterClass.ElectionRequest.newBuilder()
                                            .setTaxiId(TaxisSingleton.getInstance().getCurrentTaxi().getId())
                                            .setRideId(ride.getId())
                                            .setBattery(TaxisSingleton.getInstance().getCurrentTaxi().getBatteryLevel())
                                            .setDistrict(TaxisSingleton.getInstance().getCurrentTaxi()
                                                    .getCoordinate().getX())
                                            .setStartX(ride.getStartPosition().getX())
                                            .setStartY(ride.getStartPosition().getY())
                                            .setEndX(ride.getEndPosition().getX())
                                            .setEndY(ride.getEndPosition().getY())
                                            .build();

                                    GrcpOuterClass.ElectionResponse res;
                                    try {
                                        res = stub.election(req);

                                        // TODO: Gestire la risposta
                                        if (res.getResult().equals("OK")) {
                                            count++; // incremento del count ad ogni risposta
                                        } else {
                                            TaxisSingleton.getInstance().getCurrentTaxi().setPartecipant(false);

                                            break; // Non vincerò sicuramente io l'elezione, termino ???
                                        }
                                        //System.out.println(res);
                                        if (count == otherTaxiList.size()) {
                                            System.out.println("GESTISCO IO LA RIDE: " + ride.getId());
                                            TaxisSingleton.getInstance().getCurrentTaxi().setRiding(true);

                                            // AVVISO SETA CHE HO PRESO IN CARICA LA RICHIESTA

                                            String payload = "Ride ID: " + ride.getId() + " managed by Taxi: " +  TaxisSingleton.getInstance().getCurrentTaxi().getId();
                                            MqttMessage messageRide = new MqttMessage(payload.getBytes());
                                            messageRide.setQos(qos);
                                            client.publish("seta/smartcity/rides/accomplished/"+ride.getId(),messageRide);

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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

    public static void disconnect(){
        //System.out.println("disconnectClient()");
        try {
            client.disconnect();
        } catch (MqttException e) {
            System.out.println("disconnectClient - Errore: " + e);
        }
        System.out.println("MQTT Client disconnected!");
    }

}
