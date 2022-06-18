package smartcity;

import org.eclipse.paho.client.mqttv3.MqttClient;
import utilis.RideGenerator;
import utilis.SetaSubscriber;

public class Seta {
    public static void main(String[] args) {

        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String topic = "seta/smartcity/rides/district";
        int qos = 2;

        /* THREAD ride managed */
        SetaSubscriber setaSubscriber = new SetaSubscriber();
        setaSubscriber.start();

        /* THREAD ride generator */
        RideGenerator rideGenerator = new RideGenerator(clientId,topic,qos,broker);
        rideGenerator.start();

    }
}