package utilis;

import models.Coordinate;
import models.Ride;
import models.RidesQueue;
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
        Coordinate startCoordinateRide1;
        Coordinate destinationCoordinateRide1;

        Coordinate startCoordinateRide2;
        Coordinate destinationCoordinateRide2;

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setMaxInflight(100);

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

            try {

                id = i;

                int min = 0, max = 9;
                int ride1_x1 = 0;
                int ride1_x2 = 0;
                int ride1_y1 = 0;
                int ride1_y2 = 0;

                int ride2_x1 = 0;
                int ride2_x2 = 0;
                int ride2_y1 = 0;
                int ride2_y2 = 0;


                while (ride1_x1 == ride1_x2 && ride1_y1 == ride1_y2) {
                    ride1_x1 = (int) (Math.random() * ((max - min) + 1) + min);
                    ride1_y1 = (int) (Math.random() * ((max - min) + 1)) + min;
                    ride1_x2 = (int) (Math.random() * ((max - min) + 1)) + min;
                    ride1_y2 = (int) (Math.random() * ((max - min) + 1)) + min;
                }

                while (ride2_x1 == ride2_x2 && ride2_y1 == ride2_y2) {
                    ride2_x1 = (int) (Math.random() * ((max - min) + 1) + min);
                    ride2_y1 = (int) (Math.random() * ((max - min) + 1)) + min;
                    ride2_x2 = (int) (Math.random() * ((max - min) + 1)) + min;
                    ride2_y2 = (int) (Math.random() * ((max - min) + 1)) + min;
                }

                startCoordinateRide1 = new Coordinate(ride1_x1, ride1_y1);
                destinationCoordinateRide1 = new Coordinate(ride1_x2, ride1_y2);

                startCoordinateRide2 = new Coordinate(ride2_x1, ride2_y1);
                destinationCoordinateRide2 = new Coordinate(ride2_x2, ride2_y2);


                int district1 = startCoordinateRide1.getDistrict();
                int district2 = startCoordinateRide2.getDistrict();

                Ride ride1 = new Ride(id, startCoordinateRide1, destinationCoordinateRide1);
                String data1 = ride1.toString();

                Ride ride2 = new Ride(id, startCoordinateRide2, destinationCoordinateRide2);
                String data2 = ride2.toString();

                MqttMessage message1 = new MqttMessage(data1.getBytes());
                message1.setQos(qos);

                MqttMessage message2 = new MqttMessage(data2.getBytes());
                message2.setQos(qos);

                client.publish(topic+district1, message1);
                client.publish(topic+district2, message2);
                //client.publish(topic+1, message);
                RidesQueue.getInstance().addPending(ride1);
                RidesQueue.getInstance().addPending(ride2);
                System.out.println("NEW RIDE for TOPIC: " + topic+district1);
                System.out.println("NEW RIDE for TOPIC: " + topic+district2);


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