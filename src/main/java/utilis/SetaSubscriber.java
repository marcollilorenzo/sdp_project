package utilis;

import models.Coordinate;
import models.Ride;
import models.RidesQueue;
import org.eclipse.paho.client.mqttv3.*;
import smartcity.TaxisSingleton;

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
            connOpts.setMaxInflight(500);

            client.connect(connOpts);

            client.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) throws IOException, MqttException {
                    // Ride accomplished
                    if (topic.split("/")[3].equals("accomplished")){

                        RidesQueue.getInstance().removePendingRides(Integer.parseInt(topic.split("/")[4]));
                        String receivedMessage = new String(message.getPayload());
                        System.out.println(receivedMessage);

                    }else if (topic.split("/")[3].equals("free")){ // when taxi is free

                        // Un taxi si è liberato, posso prendere una ride dalla coda
                        String info = topic.split("/")[4];
                        int taxiId = Integer.parseInt(info.split("-")[0]);
                        int district = Integer.parseInt(info.split("-")[1]);
                        String receivedMessage = new String(message.getPayload());
                        System.out.println(receivedMessage);


                        Ride ride = RidesQueue.getInstance().takeByDistict(district);

                        if(ride != null){
                            // republic ride
                            String data = ride.toString();

                            MqttMessage newMessage = new MqttMessage(data.getBytes());
                            message.setQos(qos);

                            client.publish("seta/smartcity/rides/district"+district, newMessage);
                            //client.publish(topic+1, message);
                            RidesQueue.getInstance().addPending(ride);
                            System.out.println("REPUBLIC RIDE: "+ride.getId()+" for TOPIC: seta/smartcity/rides/district" +district);
                        }


                    }else{ // other topic

                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println(cause);
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
