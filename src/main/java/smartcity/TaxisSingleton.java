package smartcity;

import models.Coordinate;
import models.Taxi;
import models.Taxis;
import simulators.Measurement;

import java.util.ArrayList;
import java.util.List;

public class TaxisSingleton {
    private List<Taxi> taxiList;
    private List<Measurement> pollutionMeasurementList;
    private Taxi currentTaxi;

    private static TaxisSingleton instance;

    // Constructor
    private TaxisSingleton() {
        taxiList = new ArrayList<Taxi>();
        pollutionMeasurementList = new ArrayList<Measurement>();
        currentTaxi = new Taxi();
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
            taxiList.notify();
        }
    }

    public Taxi getCurrentTaxi() {
        return currentTaxi;
    }

    public void setCurrentTaxi(Taxi currentTaxi) {
        this.currentTaxi = currentTaxi;
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

    // GET POSITION BY TAXI ID
    public Taxi getPositionByTaxiId(int taxiId){
        synchronized (taxiList){
            Taxi t = taxiList.stream()
                    .filter(a -> a.getId() == taxiId)
                    .findAny()
                    .orElse(null);
            if( t == null){
                return null;
            }else{
                return t;
            }
        }
    }
}
