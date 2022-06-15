package smartcity;

import models.Coordinate;
import models.Ride;
import models.RidesQueue;
import org.eclipse.paho.client.mqttv3.*;

public class TaxiSubscriber extends Thread{

    private static MqttClient client;
    private int district;
    private String topic;

    public TaxiSubscriber(int district){
        this.district = district;
        this.topic = "seta/smartcity/rides/district"+district;
    }

    public int getDistrict() {
        return this.district;
    }

    public void setDistrict(int district) {
        this.district = district;
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

                    public void messageArrived(String topic, MqttMessage message) {

                        String receivedMessage = new String(message.getPayload()); // da binario a stringa

                        int id = Integer.parseInt(receivedMessage.split(",")[0].split("=")[1]);
                        int x1 = Integer.parseInt(receivedMessage.split(",")[1].split("=")[1].split("--")[0]);
                        int y1 = Integer.parseInt(receivedMessage.split(",")[1].split("=")[1].split("--")[1]);
                        int x2 = Integer.parseInt(receivedMessage.split(",")[2].split("=")[1].split("--")[0]);
                        int y2 = Integer.parseInt(receivedMessage.split(",")[2].split("=")[1].split("--")[1].split("}")[0]);

                        Coordinate rit = new Coordinate(x1, y1);
                        Coordinate con = new Coordinate(x2, y2);
                        Ride ride = new Ride(id, rit, con);

                     //   System.out.println(ride.toString());

                        // add ride to queue
                        RidesQueue.getInstance().add(ride);


                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }

                    @Override
                    public void connectionLost(Throwable cause) {
                        System.out.println("Connection Lost");
                    }
                });

                client.subscribe(topic, qos);
                System.out.println("Subscribed to topics : " + topic);


            } catch (MqttException e) {
                System.out.println("MqttException");
            }
        }

}
