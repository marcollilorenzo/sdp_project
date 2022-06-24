package utilis;

import models.Coordinate;
import models.Ride;
import models.RidesQueue;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.util.ArrayList;

public class SetaSubscriber extends Thread{

    private static MqttClient client;
    private ArrayList<String> topics = new ArrayList<>();

    public SetaSubscriber() {
        topics.add("seta/smartcity/rides/accomplished/#");
        topics.add("seta/smartcity/taxis/free/#");
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

                public void messageArrived(String topic, MqttMessage message) throws IOException {
                    // Ride accomplished
                    if (topic.split("/")[3].equals("accomplished")){

                        RidesQueue.getInstance().removePendingRides(Integer.parseInt(topic.split("/")[4]));
                        String receivedMessage = new String(message.getPayload());
                        System.out.println(receivedMessage);

                    }else if (topic.split("/")[3].equals("free")){ // when taxi is free

                        // Un taxi si è liberato, posso prenderne una dalla coda
                        int taxiId = Integer.parseInt(topic.split("/")[4]);


                    }else{ // other topic

                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection Lost");
                }
            });

            for (String topic : topics){
                client.subscribe(topic, qos);
                System.out.println("Subscribed to topics : " + topic);
            }

        } catch (MqttException e) {
            System.out.println("MqttException");
        }
    }

}
