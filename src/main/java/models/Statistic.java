package models;

import simulators.Measurement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Statistic {

    // data
    private double km;
    private int taxiID;
    private long timestamp;
    private int batteryLevel;
    private int ride;
    private double averageListPollution;

    // Costruttore vuoto
    public Statistic(){}

    public Statistic(int taxiID, long timestamp, int batteryLevel, double km, int ride, double averageListPollution) {
        this.taxiID=taxiID;
        this.timestamp=timestamp;
        this.batteryLevel = batteryLevel;
        this.km = km;
        this.ride = ride;
        this.averageListPollution = averageListPollution;
    }


    public double getKm() {
        return km;
    }
    public void setKm(double km) {
        this.km = km;
    }

    public int getTaxiID() {
        return taxiID;
    }
    public void setTaxiID(int taxiID) {
        this.taxiID = taxiID;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }
    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getRide() {
        return ride;
    }
    public void setRide(int ride) {
        this.ride = ride;
    }

    public double getAverageListPollution() {
        return averageListPollution;
    }
    public void setAverageListPollution(double averageListPollution) {
        this.averageListPollution = averageListPollution;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "km=" + km +
                ", taxiID=" + taxiID +
                ", timestamp=" + timestamp +
                ", batteryLevel=" + batteryLevel +
                ", ride=" + ride +
                ", averageList=" + averageListPollution +
                '}';
    }
}
