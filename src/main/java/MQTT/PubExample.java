package MQTT;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PubExample {

    public static void main(String[] args) {
        // Create an Mqtt client
        String broker = "tcp://localhost:1883";
        String clientId = "tem_sens_01";
        String topic = "home/sensors/bedroom/temperature";
        int qos = 1;

        {
            try {
                MqttClient mqttClient = new MqttClient(broker, clientId);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true); // false = the broker stores all subscriptions for the client and all missed messages for the client that subscribed with a Qos level 1 or 2
                // connOpts.setKeepAliveInterval(1000); //longest period of time that the broker and client can endure without sending a message.
                // connOpts.setWill(); //If the client disconnects ungracefully, the broker sends the LWT message on behalf of the client.
                // Connect the client
                System.out.println(clientId + " Connecting Broker" + broker);
                mqttClient.connect(connOpts);
                System.out.println(clientId + " Connected");

                // Create a Mqtt message
                String randTemp = String.valueOf(18 + (Math.random() * 4)); // create a random temperature between 18 and 22 degrees
                MqttMessage message = new MqttMessage(randTemp.getBytes());

                // Set the QoS on the Message
                message.setQos(qos);
                System.out.println(clientId + " Publishing message: " + randTemp + " ...");
                mqttClient.publish(topic, message);
                System.out.println(clientId + " Message published");

                // Disconnect the client
                mqttClient.disconnect();
                System.out.println("Publisher " + clientId + " disconnected");

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
}
