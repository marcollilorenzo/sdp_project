package models;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@XmlRootElement
public class Taxi {

    // start information
    private Integer id;
    private Integer port;
    private String serverAddress;

    // other information
    private Double batteryLevel;
    private Coordinate coordinate;
    private List<Taxi> allTaxi;

    public Taxi(){}

    public Taxi(Integer id, Integer port, String serverAddress) {
        this.id = id;
        this.port = port;
        this.serverAddress = serverAddress;
        this.batteryLevel = 100.00;
    }

    public Double getBatteryLevel() {
        return batteryLevel;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setAllTaxi(List<Taxi> allTaxi) {
        this.allTaxi = allTaxi;
    }
}
