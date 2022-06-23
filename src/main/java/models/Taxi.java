package models;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@XmlRootElement
public class Taxi {

    // start information
    private int id;
    private int port;
    private String serverAddress;

    // other information
    private int batteryLevel;
    private Coordinate coordinate;
    private List<Taxi> allTaxi;

    // status
    private boolean isRiding = false;
    private boolean isRecharging = false;
    private boolean isPartecipant = false;



    public Taxi(){}

    public Taxi(int id, int port, String serverAddress) {
        this.id = id;
        this.port = port;
        this.serverAddress = serverAddress;
    }

    public Taxi(int id, int port, String serverAddress, int batteryLevel, Coordinate coordinate) {
        this.id = id;
        this.port = port;
        this.serverAddress = serverAddress;
        this.batteryLevel = batteryLevel;
        this.coordinate = coordinate;
    }

    public Integer getId() {
        return id;
    }

    public int getPort() { return port;}
    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServerAddress() { return serverAddress;}
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public List<Taxi> getAllTaxi() { return allTaxi;}
    public void setAllTaxi(List<Taxi> allTaxi) { this.allTaxi = allTaxi;}

    public Coordinate getCoordinate() {
        return coordinate;
    }
    public void setCoordinate(Coordinate coordinate) { this.coordinate = coordinate;}

    public int getBatteryLevel() {
        return batteryLevel;
    }
    public void setBatteryLevel(int batteryLevel) { this.batteryLevel = batteryLevel;}

    public boolean isRiding() {
        return isRiding;
    }
    public void setRiding(boolean riding) {
        isRiding = riding;
    }

    public boolean isRecharging() {
        return isRecharging;
    }
    public void setRecharging(boolean recharging) {
        isRecharging = recharging;
    }

    public boolean isPartecipant() {
        return isPartecipant;
    }

    public void setPartecipant(boolean partecipant) {
        isPartecipant = partecipant;
    }
}
