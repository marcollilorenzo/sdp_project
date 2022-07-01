package smartcity;

import com.example.taxis.GrcpGrpc;
import com.example.taxis.GrcpOuterClass;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import models.Coordinate;
import models.Statistic;
import models.Taxi;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttException;
import simulators.AdminMeasurement;
import simulators.Measurement;
import simulators.PM10Simulator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TaxiProcess {

    private static Server serverGrcp;
    private static TaxiSubPub threadSubPub;
    private static int myID;

    /*
    TODO:
        2. QUERY CLIENT
     */


    public static void main(String[] args) throws IOException {

        initTaxi(); // register taxi to list and start to acquire statistic from pollution sensor
        manageInput(); // manage keyboard input for quit the taxis

    }

    public static void initTaxi() {
        // Create random info
        int taxiId = (int) Math.floor(Math.random() * (1000 - 1 + 1) + 1);
        int taxiPort = (int) Math.floor(Math.random() * (3000 - 1000 + 1) + 1000);
        String serverAddressToSend = "localhost";

        // Variable for REST call
        String input;
        ClientResponse result;
        JSONArray response = null;


        // Call administrator server for add the Taxi 👮🏼‍
        try {
            Client client = Client.create();
            WebResource webResource = client.resource("http://localhost:1337/taxis/add");

            input = "{\"id\":\"" + taxiId + "\",\"port\":\"" + taxiPort + "\",\"serverAddress\":\"" + serverAddressToSend + "\"}";

            result = webResource.type("application/json").post(ClientResponse.class, input);
            response = result.getEntity(JSONArray.class);

            if (result.getStatus() == 200) {

                myID = taxiId;

                // Save on singleton process
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = (JSONObject) response.get(i);
                    int id = (int) jsonObject.get("id");

                    int port = (int) jsonObject.get("port");
                    String serverAddress = (String) jsonObject.get("serverAddress");

                    int x = Integer.parseInt(jsonObject.get("coordinate").toString().substring(5, 6));
                    int y = Integer.parseInt(jsonObject.get("coordinate").toString().substring(11, 12));
                    Coordinate coordinate = new Coordinate(x, y);

                    int batteryLevel = (int) jsonObject.get("batteryLevel");

                    Taxi taxi = new Taxi(id, port, serverAddress, batteryLevel, coordinate);

                    // Se sono io non aggiungo il taxi alla lista ma come taxi corrente
                    if (taxi.getId() == myID) {
                        System.out.println("MY ID: " + taxi.getId());
                        TaxisSingleton.getInstance().setCurrentTaxi(taxi);
                    } else {
                        TaxisSingleton.getInstance().addTaxi(taxi);
                    }
                }

                System.out.println("THERE ARE " + TaxisSingleton.getInstance().getTaxiList().size() + " OTHER TAXI");


                // Start to send statistics 📈
                acquirePollutionStats();

                // Start server GRCP
                startServerGrcp();

                //register topic
                taxiBroker();

                // send stat to server every 15 second
                sendStatistics();
            }


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void acquirePollutionStats() {
        System.out.println("\nSTART TO ACQUIRE POLLUTION STATS");
        AdminMeasurement pollutionBuffer = new AdminMeasurement();
        PM10Simulator pm10Simulator = new PM10Simulator(pollutionBuffer);
        pm10Simulator.start(); // start sensor

        ArrayList<Measurement> measurementList = new ArrayList<>(); // lista delle misurazioni

        // THREAD THAT MANAGE THE MEASURMENT OF POLLUTION SENSOR
        new Thread(() -> {
            while (true) {

                // readAllAndClean wait if not have just 8 measurement
                measurementList.addAll(pollutionBuffer.readAllAndClean());

                double sum = 0;
                long timestamp = 0;
                int measurementId = 0;
                for (Measurement m : measurementList) {
                    sum += m.getValue();
                    timestamp = m.getTimestamp();
                }

                TaxisSingleton.getInstance().addAverageList(
                        new Measurement("pm10-" + measurementId++, "PM10", sum / 8, timestamp) // compute average
                );
                measurementList.clear(); //sum = 0

            }
        }).start();

    }

    private static void startServerGrcp() throws IOException {
        try {
            serverGrcp = ServerBuilder
                    .forPort(TaxisSingleton.getInstance().getCurrentTaxi().getPort())
                    .addService(new GrcpImpl())
                    .build();
            serverGrcp.start();
            System.out.println("START GRPC");

            //AVVISO TUTTI GLI ALTRI TAXI
            if (!TaxisSingleton.getInstance().getTaxiList().isEmpty()) {
                ArrayList<Taxi> otherTaxiList = TaxisSingleton.getInstance().getTaxiList();
                System.out.println(otherTaxiList);
                for (Taxi t : otherTaxiList) {
                    System.out.println("Send Welcome to Taxi ID: " + t.getId());
                    final ManagedChannel channel = ManagedChannelBuilder
                            .forTarget(t.getServerAddress() + ":" + t.getPort())
                            .usePlaintext()
                            .build();

                    GrcpGrpc.GrcpBlockingStub stub = GrcpGrpc.newBlockingStub(channel);

                    GrcpOuterClass.WelcomeRequest req = GrcpOuterClass.WelcomeRequest.newBuilder()
                            .setId(TaxisSingleton.getInstance().getCurrentTaxi().getId())
                            .setPort(TaxisSingleton.getInstance().getCurrentTaxi().getPort())
                            .setX(TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate().getX())
                            .setY(TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate().getY())
                            .setBattery(TaxisSingleton.getInstance().getCurrentTaxi().getBatteryLevel())
                            .setAddress(TaxisSingleton.getInstance().getCurrentTaxi().getServerAddress())
                            .build();

                    GrcpOuterClass.WelcomeResponse res;
                    try {
                        res = stub.welcome(req);
                        System.out.println(res);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    channel.shutdownNow();
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void taxiBroker() throws MqttException {

        Coordinate coordinate = TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate();
        if (coordinate != null) {
            int myInitDistrict = coordinate.getDistrict();

            //get district number
            threadSubPub = new TaxiSubPub(myInitDistrict);
            threadSubPub.start();

        }

    }

    private static void sendStatistics(){
        System.out.println("\nSEND STATISTICS");

        new Thread(()->{
            while(true) {

                // Variable for REST call
                String input;
                ClientResponse result;
                JSONArray response = null;


                /*
                    STATISTICHE DA INVIARE
                    1. Numero di chilometri percorsi
                    2. Numero di rides prese in carico
                    3. Lista della media delle misurazioni di inquinamento

                    Ogni statistica deve essere inviata al server con Taxi ID, Timestap, Livello batteria
                 */

                double km = TaxisSingleton.getInstance().getKm();
                int numberRides = TaxisSingleton.getInstance().getRides();
                Date date = new Date();
                long taxiTimestamp = date.getTime();

                List<Measurement> measurements = TaxisSingleton.getInstance().getPollutionMeasurementList();
                float singleAveragePollution = 0;

                for (Measurement m: measurements) { // media pollution della singola statistica
                    singleAveragePollution += m.getValue();
                }

                // STOP 15 SECONDS
                try {

                    Client client = Client.create();
                    WebResource webResource = client.resource("http://localhost:1337/stat/add");



                    Statistic s = new Statistic(
                            TaxisSingleton.getInstance().getCurrentTaxi().getId(),
                            taxiTimestamp,
                            TaxisSingleton.getInstance().getCurrentTaxi().getBatteryLevel(),
                            km,
                            numberRides,
                            TaxisSingleton.getInstance().getPollutionMeasurementListValue().stream()
                                    .mapToDouble(a -> a)
                                    .average().orElse(0)
                    );

                    input = new Gson().toJson(s);

                  /*  input = "{\"km\":\"" + km +
                            "\",\"taxiID\":\"" + TaxisSingleton.getInstance().getCurrentTaxi().getId() +
                            "\",\"timestamp\":\"" + taxiTimestamp +
                            "\",\"batteryLevel\":\"" + TaxisSingleton.getInstance().getCurrentTaxi().getBatteryLevel() +
                            "\",\"ride\":\"" + numberRides +
                            "\",\"averageListPollution\":\"" + TaxisSingleton.getInstance().getPollutionMeasurementListValue().stream()
                            .mapToDouble(a -> a)
                            .average().orElse(0) + "\"}"; */

                    result = webResource.type("application/json").post(ClientResponse.class, input);


                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } // while
        }).start();

    }

    private static void manageInput() {
        new Thread(() -> { // lambda expression
            while (true) {
                Scanner scanner = new Scanner(System.in);
                //System.out.print("⏹ Send quit to stop the drone process...\n");
                String input = scanner.next();
                if (input.equals("quit")) {
                    try {
                        exitFromNetwork();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(input.equals("re")){
                    System.out.println("\n RICHIESTA RICARICA");
                    try {
                        if(TaxisSingleton.getInstance().isRiding()){
                            synchronized (TaxisSingleton.getInstance().getDeliveryInProgressLock()) {
                                try {
                                    System.out.println("Aspetta, sto effettuando una ride");
                                    TaxisSingleton.getInstance().getDeliveryInProgressLock().wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if(TaxisSingleton.getInstance().isPartecipant()){
                            synchronized(TaxisSingleton.getInstance().getParticipantElectionLock()){
                                try {
                                    System.out.println("Aspetta, sono in corso di un'elezione");
                                    TaxisSingleton.getInstance().getParticipantElectionLock().wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                        if(TaxisSingleton.getInstance().isRecharging()){
                            synchronized (TaxisSingleton.getInstance().getChargeBatteryLock()){
                                try {
                                    System.out.println("Aspetta, mi sto già caricando");
                                    TaxisSingleton.getInstance().getChargeBatteryLock().wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        System.out.println("RICHIESTA RICARICA PRESA IN CARICO");
                        recharge();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } // while
        }).start();
    }

    private static void exitFromNetwork() throws IOException {

        if(TaxisSingleton.getInstance().isQuit()){return;}
        TaxisSingleton.getInstance().setQuit(true);


        /*
            1. Controllo se Taxi è in un'elezione
            2. Controllo se Taxi sta effettuando una corsa
            3. Disconettere broker MQTT
            4. Invio statistiche al Server
            5. Stop Server GRPC
            6. Eliminazione dal Server
         */

        // 1. Controllo se taxi è già in un'elezione
        synchronized (TaxisSingleton.getInstance().getParticipantElectionLock()){
            while(TaxisSingleton.getInstance().isPartecipant()){
                try {
                    System.out.println("Sono in un elezione, aspetto di finirla e poi esco");
                    TaxisSingleton.getInstance().getParticipantElectionLock().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 2. Controllo se Taxi sta effettuando una corsa
        synchronized (TaxisSingleton.getInstance().getDeliveryInProgressLock()){
            while(TaxisSingleton.getInstance().isRiding()){
                try {
                    System.out.println("Sto consegnando, non posso uscire");
                    TaxisSingleton.getInstance().getDeliveryInProgressLock().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 3. Disconettere broker MQTT
        TaxiSubPub.disconnect();


        // 5. Stop Server GRPC
        try {
            serverGrcp.shutdownNow();
            System.out.println("GRCP stop!");
        } catch (Exception e){
            System.out.println("Error GRCP stop: " + e);
        }

        // 6. Eliminazione dal Server
        int id = TaxisSingleton.getInstance().getCurrentTaxi().getId();
        try {
            Client client = Client.create();
            WebResource webResource = client.resource("http://localhost:1337/taxis/delete/"+id);

            ClientResponse result = webResource.type("application/json").delete(ClientResponse.class);

            if(result.getStatus() == 200){
                System.out.println("Delete from network OK");
                System.exit(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void recharge() throws InterruptedException {

    int countRecharge;
        int myDistrict = TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate().getDistrict();
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
                    .setDistrict(myDistrict)
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
                System.out.println("🗑 NON RIESCO A CONTATTARE IL TAXI: " + t.getId() + " LO ELIMINO");
            }

            channel.shutdownNow();

        }

        // MI POSSO RICARICARE
        if (countRecharge == otherTaxiList.size()) ;
        {

            System.out.println("HO VINTO ELEZIONE PER LA STAZIONE DI RICARICA");

            TaxisSingleton.getInstance().setRecharging(2); // la uso

            System.out.println("\nMI STO RICARICANDO 💪🏻");
            Thread.sleep(10000);
            System.out.println("RICARICA COMPLETATA");

            // Aggiorno livello batteria e posizione

            ArrayList<Integer> newCordinateArray = TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate().getStationCoordinateByDistrict();
            Coordinate newCordinate = new Coordinate(newCordinateArray.get(0),newCordinateArray.get(1)); // prendo il distretto da dove sono partito per ricaricarmi

            // Non sto più usando la stazione di ricarica
            TaxisSingleton.getInstance().setRecharging(0);

            // aggiorno le mie info
            TaxisSingleton.getInstance().getCurrentTaxi().setBatteryLevel(100);
            TaxisSingleton.getInstance().getCurrentTaxi().setCoordinate(newCordinate);

            // send my new info to all other taxi
            for (Taxi t : otherTaxiList) {
                final ManagedChannel channel = ManagedChannelBuilder
                        .forTarget(t.getServerAddress() + ":" + t.getPort())
                        .usePlaintext()
                        .build();

                GrcpGrpc.GrcpBlockingStub stub = GrcpGrpc.newBlockingStub(channel);

                GrcpOuterClass.UpdateTaxiInfoRequest req = GrcpOuterClass.UpdateTaxiInfoRequest.newBuilder()
                        .setBattery(100)
                        .setX(newCordinate.getX())
                        .setY(newCordinate.getY())
                        .setId(TaxisSingleton.getInstance().getCurrentTaxi().getId())
                        .build();

                GrcpOuterClass.UpdateTaxiInfoResponse res;
                try {
                    res = stub.updateTaxiInfo(req);
                } catch (Exception e) {
                    TaxisSingleton.getInstance().removeTaxiById(t.getId());
                    System.out.println("🗑 NON RIESCO A CONTATTARE IL TAXI: " + t.getId() + " LO ELIMINO");
                }

                channel.shutdownNow();
            }

            synchronized (TaxisSingleton.getInstance().getChargeBatteryLock()) {
                TaxisSingleton.getInstance().getChargeBatteryLock().notifyAll();
            }


        }


    }
}
