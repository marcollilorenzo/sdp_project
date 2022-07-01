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

    // data
    private volatile int rides;
    private volatile double km;

    // status
    private boolean isRiding = false;
    private int isRecharging = 0;
    private boolean isPartecipant = false;
    private boolean isQuit = false;
    private int idRideInProgress;
    private int idRidePartecipant;

    // thread locking
    private Object chargeBatteryLock = new Object();
    private Object deliveryInProgressLock = new Object();
    private Object participantElectionLock = new Object();

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

    // Taxi things
    public void addTaxi(Taxi t){
        synchronized (taxiList) {
            taxiList.add(t);
            taxiList.notify();
        }
    }
    public void removeTaxiById(int taxiID){
        synchronized (taxiList){
            taxiList.removeIf((t -> t.getId() == taxiID));
            taxiList.notify();
        }
    }
    public Taxi getCurrentTaxi() {
        return currentTaxi;
    }
    public void setCurrentTaxi(Taxi currentTaxi) {
        this.currentTaxi = currentTaxi;
    }
    public Coordinate getPositionByTaxiId(int taxiId){
        synchronized (taxiList){
            Taxi t = taxiList.stream()
                    .filter(a -> a.getId() == taxiId)
                    .findAny()
                    .orElse(null);
            if( t == null){
                return null;
            }else{
                return t.getCoordinate();
            }
        }
    }
    public void updateTaxiById(int taxiId, int battery, Coordinate coordinate){
        synchronized (taxiList){
            Taxi t = taxiList.stream()
                    .filter(a -> a.getId() == taxiId)
                    .findAny()
                    .orElse(null);
            if( t != null){
                t.setBatteryLevel(battery);
                t.setCoordinate(coordinate);
            }
            taxiList.notify();
        }
    }

    // pollution
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
    public List<Double> getPollutionMeasurementListValue() {
        ArrayList<Double> temp = new ArrayList<>();
        synchronized (pollutionMeasurementList) {
            for (Measurement m: pollutionMeasurementList) {
                temp.add(m.getValue());
            }
        }
        return temp;
    }
    public void clearPollutionMeasurementList() {
        synchronized (pollutionMeasurementList) {
            pollutionMeasurementList.clear();
        }
    }

    // statistic
    public void addKm(int km){
        this.km += km;
    }
    public double getKm() {
        return km;
    }

    public void addRide(){
        rides++;
    }
    public int getRides() {
        return rides;
    }


    // lock
    public Object getChargeBatteryLock() {
        return chargeBatteryLock;
    }
    public void setChargeBatteryLock(Object chargeBatteryLock) {
        this.chargeBatteryLock = chargeBatteryLock;
    }

    public Object getDeliveryInProgressLock() {
        return deliveryInProgressLock;
    }
    public void setDeliveryInProgressLock(Object deliveryInProgressLock) {
        this.deliveryInProgressLock = deliveryInProgressLock;
    }

    public Object getParticipantElectionLock() {
        return participantElectionLock;
    }
    public void setParticipantElectionLock(Object participantElectionLock) {
        this.participantElectionLock = participantElectionLock;
    }


    // status
    public boolean isRiding() {
        return isRiding;
    }
    public void setRiding(boolean riding) {
        isRiding = riding;
    }

    public boolean isRecharging() {
        if(isRecharging == 0){
            return false;
        }else{
            return true;
        }

    }
    public void setRecharging(int recharging) {
        isRecharging = recharging;
    }
    public int getRechargingStatus(){
        return this.isRecharging;
    }

    public boolean isPartecipant() {
        return isPartecipant;
    }
    public void setPartecipant(boolean partecipant) {
        isPartecipant = partecipant;
    }

    public int getIdRideInProgress() {
        return idRideInProgress;
    }
    public void setIdRideInProgress(int idRideInProgress) {
        this.idRideInProgress = idRideInProgress;
    }

    public int getIdRidePartecipant() {
        return idRidePartecipant;
    }
    public void setIdRidePartecipant(int idRidePartecipant) {
        this.idRidePartecipant = idRidePartecipant;
    }

    public boolean isQuit() {
        return isQuit;
    }
    public void setQuit(boolean quit) {
        isQuit = quit;
    }
}
