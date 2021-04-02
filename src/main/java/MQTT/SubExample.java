package MQTT;

import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;
import java.util.Scanner;

public class SubExample {
    public static void main(String[] args) {
        // Create an Mqtt client


        System.out.println("Subscriber initializing...");
        String broker = "tcp://localhost:1883";
        String clientId = "sub_01";
        String topic = "home/sensors/bedroom/temperature";
        int qos = 1;

        try {
            // Create an Mqtt client
            MqttAsyncClient mqttClient = new MqttAsyncClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Connect the client
            System.out.println(clientId + " Connecting to " + broker);
            IMqttToken token = mqttClient.connect(connOpts);
            token.waitForCompletion();
            System.out.println(clientId + " Connected");


            // Callback
            mqttClient.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) {
                    // Called when a message arrives from the server that matches any subscription made by the client
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());
                    System.out.println(clientId + " Received a Message!" +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + receivedMessage +
                            "\n\tQoS:     " + message.getQos() + "\n");
                    System.out.println("\n ***  Press return to exit *** \n");
                }

                public void connectionLost(Throwable cause) {
                    System.out.println(clientId + " Connectionlost! " + cause.getMessage());
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }

            });

            // Subscribe client to the topic filter and a QoS level of 1
            System.out.println("Subscribing client "+clientId+" to topic: " + topic);
            mqttClient.subscribe(topic, qos);
            System.out.println(clientId + " Subscribed");

            System.out.println("\n ***  Press a random key to exit *** \n");
            Scanner command = new Scanner(System.in);
            command.nextLine();
            if (mqttClient.isConnected())
                mqttClient.disconnect();

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}
