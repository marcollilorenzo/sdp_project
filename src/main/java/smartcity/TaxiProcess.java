package smartcity;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import models.Coordinate;
import models.Taxi;
import models.Taxis;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import simulators.AdminMeasurement;
import simulators.Measurement;
import simulators.PM10Simulator;

import java.util.ArrayList;
import java.util.List;

public class TaxiProcess {

    public static void main(String[] args) {

        registerTaxi(); // register texi to list and start to send statistics

    }


    public static void registerTaxi(){
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
                        TaxisSingleton.getInstance().addTaxi(taxi);
                }

                System.out.println(TaxisSingleton.getInstance().getTaxiList().size() + " TAXI ADDED TO LIST");

                // Start to send statistics 📈
                acquirePollutionStats();

            }


        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static void acquirePollutionStats(){
        System.out.println("START TO ACQUIRE POLLUTION STATS");
        AdminMeasurement pollutionBuffer = new AdminMeasurement();
        PM10Simulator pm10Simulator = new PM10Simulator(pollutionBuffer);
        pm10Simulator.start();

    }

}
