package utilis;

import models.Coordinate;
import models.Ride;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RideGenerator extends Thread{

    private MqttClient client;
    private String clientId;
    private String topic;
    private int qos;
    private String broker;

    public RideGenerator(String clientId, String topic, int qos, String broker) {
        this.clientId = clientId;
        this.topic = topic;
        this.qos = qos;
        this.broker = broker;
    }

    @Override
    public void run() {
        super.run();

        int id;
        Coordinate startCoordinate;
        Coordinate destinationCoordinate;

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Connect the client
            System.out.println(clientId + " Connecting Broker " + broker);
            client.connect(connOpts); // bloccante
            System.out.println(clientId + " Connected");
        } catch (MqttException e) {
            e.printStackTrace();
        }

        int i = 0;
        // PUBLISH RIDE TO RANDOM TOPIC FROM [1-4]
        while (true){

            int district = (int) (Math.random() * ((4 - 1) + 1)) + 1;

            try {

                id = i;

                int min = 0, max = 9;
                startCoordinate = new Coordinate((int) (Math.random() * ((max - min) + 1)) + min, (int) (Math.random() * ((max - min) + 1)) + min);
                destinationCoordinate = new Coordinate((int) (Math.random() * ((max - min) + 1)) + min, (int) (Math.random() * ((max - min) + 1)) + min);

                Ride ride = new Ride(id, startCoordinate, destinationCoordinate);
                String data = ride.toString();
                System.out.println(data);

                MqttMessage message = new MqttMessage(data.getBytes());
                message.setQos(qos);

                client.publish(topic+district, message);
                System.out.println("NEW RIDE for TOPIC: " + topic+district);

                Thread.sleep(5000);

                i++;
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
