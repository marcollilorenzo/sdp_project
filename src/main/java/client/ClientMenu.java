package client;

import com.google.gson.Gson;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import models.Taxi;
import models.Taxis;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import java.util.ArrayList;
import java.util.Scanner;

public class ClientMenu {

    private final Scanner scanner;
    private final Client client;
    private final String serverAddress;
    private ClientResponse response;

    public ClientMenu(Scanner scanner) {
        this.scanner = scanner;

        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getClasses().add(JacksonJsonProvider.class);
        this.client = Client.create(clientConfig);

        this.serverAddress="http://localhost:1337";
        this.response = null;
    }


    public void printMenu() {
        System.out.println("====================================================================================================================");
        System.out.println("|                                                 MENU SELECTION                                                   |");
        System.out.println("====================================================================================================================");
        System.out.println("| Options:                                                                                                         |");
        System.out.println("|    1. List of taxis present                                                                                      |");
        System.out.println("|    2. Stop Client                                                                                                |");
        System.out.println("====================================================================================================================");
        System.out.print("Select option: ");
    }

    public void selectOption(int s){
        switch (s){
            case 1:
                listTaxi();
                break;
            case 2:
                break;
            default:
                System.out.println("Command not found, try again");
        }
    }


    private void listTaxi() {

        String getPath = "/taxis/get";
        String url = serverAddress + getPath;
        WebResource webResource = client.resource(url);

        try {
            response = webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server non disponibile");
            return;
        }

        int status = response.getStatus();
        if (status != 200) {
            if (status == 204) System.out.println("Failed: la lista dei droni è vuota!");
            else System.out.println("Failed : HTTP error code : " + status);
            return;
        }

        Gson gson = new Gson();
        Taxis taxis = gson.fromJson(response.getEntity(String.class), Taxis.class);

        System.out.println("Taxis List Size " + taxis.getTaxisList().size());
        for (Taxi t : taxis.getTaxisList()){
            System.out.println("======================");
            System.out.println("Id: " + t.getId());
            System.out.println("Ip address: " + t.getServerAddress());
            System.out.println("Port: " + t.getPort());
            System.out.println("======================");
            System.out.println();
        }

    }

}
