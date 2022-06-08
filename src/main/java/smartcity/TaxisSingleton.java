package smartcity;

import models.Taxi;
import models.Taxis;
import simulators.Measurement;

import java.util.ArrayList;
import java.util.List;

public class TaxisSingleton {
    private List<Taxi> taxiList;
    private List<Measurement> pollutionMeasurementList;

    private static TaxisSingleton instance;

    // Constructor
    private TaxisSingleton() {
        taxiList = new ArrayList<Taxi>();
    }

    // Singleton
    public synchronized static TaxisSingleton getInstance(){
        if(instance==null)
            instance = new TaxisSingleton();
        return instance;
    }

    // Get taxis list
    public ArrayList<Taxi> getTaxiList(){
        synchronized(taxiList) {
            return new ArrayList<>(taxiList);
        }
    }

    // Add taxi to list
    public void addTaxi(Taxi t){
        synchronized (taxiList) {
            taxiList.add(t);
           // taxiList.notify();
        }
    }

    public List<Measurement> getPollutionMeasurementList() {
        synchronized (pollutionMeasurementList) {
            return pollutionMeasurementList;
        }
    }

    public void addAverageList(Measurement pm10) {
        synchronized (pollutionMeasurementList){
            pollutionMeasurementList.add(pm10);
        }
    }
}
