package smartcity;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import models.Taxi;
import models.Taxis;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

public class TaxiProcess {

    public static void main(String[] args) {
        // REGISTER TAXI TO LIST
        registerTaxi();

    }


    public static void registerTaxi(){
        // Create random info
        int taxiId = (int)Math.floor(Math.random()*(1000-1+1)+1);
        int taxiPort = (int)Math.floor(Math.random()*(3000-1000+1)+1000);
        String serverAddress = "localhost";

        // Variable for REST call
        String input;
        ClientResponse result;
        JSONArray response = null;


        // Call administrator server for add the Taxi 👮🏼‍
        try {
            Client client = Client.create();
            WebResource webResource = client.resource("http://localhost:1337/taxis/add");

            input = "{\"id\":\"" + taxiId + "\",\"port\":\"" + taxiPort + "\",\"serverAddress\":\"" + serverAddress + "\"}";

            result = webResource.type("application/json").post(ClientResponse.class, input);
            response = result.getEntity(JSONArray.class);

            if (result.getStatus() == 200){
                // Save new singleton
                for (int i = 0; i < response.length() ; i++) {
                    JSONObject jsonObject = (JSONObject) response.get(i);
                    int droneBattery = (int) jsonObject.get("batteryLevel");
                    System.out.println("Id: "+jsonObject.get("id"));
                    System.out.println("Port: "+jsonObject.get("port"));
                    System.out.println("Server Address: "+jsonObject.get("serverAddress"));
                    System.out.println("Coordinate: "+jsonObject.get("coordinate"));
                    System.out.println("Battery Level: "+ droneBattery);
                    System.out.println("------");
                }

                // Start to send statistics 📈

            }


        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static void acquireStats(){
        
    }

}
