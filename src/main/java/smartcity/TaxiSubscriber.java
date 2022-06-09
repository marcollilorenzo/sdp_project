package smartcity;

import com.google.gson.Gson;
import models.Coordinate;
import models.Ride;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.paho.client.mqttv3.*;

public class TaxiSubscriber extends Thread{

    private static MqttClient client;

    public TaxiSubscriber(){}

    @Override
    public void run() {
        super.run();


        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String topic = "seta/smartcity/rides/district1";
        int qos = 2;

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.connect(connOpts);


            client.setCallback(new MqttCallback() {

                // 1. messageArrived
                // ricezione di un msg su uno specifico topic sul quale eravamo sottoscritti
                public void messageArrived(String topic, MqttMessage message) throws JSONException {


                    // Called when a message arrives from the server that matches any subscription made by the client
                    //String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload()); // da binario a stringa




                    int id = Integer.parseInt(receivedMessage.split(",")[0].split("=")[1]);
                    int x1 = Integer.parseInt(receivedMessage.split(",")[1].split("=")[1].split("--")[0]);
                    int y1 = Integer.parseInt(receivedMessage.split(",")[1].split("=")[1].split("--")[1]);
                    int x2 = Integer.parseInt(receivedMessage.split(",")[2].split("=")[1].split("--")[0]);
                    int y2 = Integer.parseInt(receivedMessage.split(",")[2].split("=")[1].split("--")[1].split("}")[0]);

                    Coordinate rit = new Coordinate(x1,y1);
                    Coordinate con = new Coordinate(x2,y2);
                    Ride ride = new Ride(id, rit, con);
                  //  Orders.getInstance().add(order);
                    System.out.println(ride.toString());




                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection Lost");
                }
            });

            client.subscribe(topic,qos);
            System.out.println("Subscribed to topics : " + topic);



        } catch (MqttException e) {
                System.out.println("MqttException");
        }
    }
}
