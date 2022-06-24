package smartcity;

import com.example.taxis.GrcpGrpc;
import com.example.taxis.GrcpOuterClass;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import models.Coordinate;
import models.Taxi;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import simulators.AdminMeasurement;
import simulators.Measurement;
import simulators.PM10Simulator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TaxiProcess {

    private static Server serverGrcp;
    private static TaxiSubPub threadSubPub;
    private static int myID;

    public static void main(String[] args) throws IOException {

        initTaxi(); // register taxi to list and start to acquire statistic from pollution sensor
        manageInput(); // manage keyboard input for quit the taxis

    }

    public static void initTaxi(){
        // Create random info
        int taxiId = (int)Math.floor(Math.random()*(1000-1+1)+1);
        int taxiPort = (int)Math.floor(Math.random()*(3000-1000+1)+1000);
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

            if (result.getStatus() == 200){

                myID = taxiId;

                // Save on singleton process
                for (int i = 0; i < response.length() ; i++) {
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
                        if (taxi.getId() == myID){
                            System.out.println("MY ID: "+ taxi.getId());
                            TaxisSingleton.getInstance().setCurrentTaxi(taxi);
                        }else{
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
            }


        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static void acquirePollutionStats(){
        System.out.println("START TO ACQUIRE POLLUTION STATS");
        AdminMeasurement pollutionBuffer = new AdminMeasurement();
        PM10Simulator pm10Simulator = new PM10Simulator(pollutionBuffer);
        pm10Simulator.start(); // start sensor

        ArrayList<Measurement> measurementList = new ArrayList<>(); // lista delle misurazioni

        // THREAD THAT MANAGE THE MEASURMENT OF POLLUTION SENSOR
        new Thread(() -> {
            while(true) {

                // readAllAndClean wait if not have just 8 measurement
                measurementList.addAll( pollutionBuffer.readAllAndClean() );

                double sum = 0; long timestamp = 0; int measurementId = 0;
                for(Measurement m : measurementList){
                    sum += m.getValue();
                    timestamp = m.getTimestamp();
                }

                TaxisSingleton.getInstance().addAverageList(
                        new Measurement("pm10-"+measurementId++,"PM10",sum/8,timestamp) // compute average
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
            if (!TaxisSingleton.getInstance().getTaxiList().isEmpty()){
                ArrayList<Taxi> otherTaxiList = TaxisSingleton.getInstance().getTaxiList();
                System.out.println(otherTaxiList);
                for (Taxi t : otherTaxiList){
                    System.out.println("Send Welcome to Taxi ID: "+ t.getId());
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
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void taxiBroker() {

        Coordinate coordinate = TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate();
        if(coordinate != null){
            int myInitDistrict = coordinate.getDistrict();

            //get district number
            threadSubPub = new TaxiSubPub(myInitDistrict);
            threadSubPub.start();

        }

    }

    private static void manageInput(){
        new Thread(()->{ // lambda expression
            while(true) {
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
            } // while
        }).start();
    }

    private static void exitFromNetwork() throws IOException {

        // DISCONETT MQTT
       TaxiSubPub.disconnect();

       // DISCONNECT GRPC
        try {
            serverGrcp.shutdownNow();
            System.out.println("GRCP stop!");
        } catch (Exception e){
            System.out.println("Error GRCP stop: " + e);
        }

        // COMUNICATE EXIT TO SERVER REST
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



}
