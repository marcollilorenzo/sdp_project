package models;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Taxis {

    @XmlElement(name="taxis")

    private List<Taxi> taxiList;
    private static Taxis instance;

    // Constructor
    private Taxis() {
        taxiList = new ArrayList<Taxi>();
    }

    // Singleton
    public synchronized static Taxis getInstance(){
        if(instance==null)
            instance = new Taxis();
        return instance;
    }

    // Get taxis list
    public synchronized List<Taxi> getTaxisList(){
        return new ArrayList<>(taxiList);
    }

    // Check if ad ID in the list
    private synchronized boolean isInList(int taxiId){
        List<Taxi> taxiShallow = getTaxisList(); // shallow copy
        Taxi taxiWithEqualId = taxiShallow.stream()
                .filter(a -> a.getId() == taxiId)
                .findAny()
                .orElse(null);
        // Filter return NULL if not find a taxi with same id
        if(taxiWithEqualId == null){
            return false;
        }
        return true;
    }


    // Add Taxi to list
    public synchronized boolean add(Taxi taxi){

        Boolean isInList = isInList(taxi.getId());
        if(isInList){
            return false;
        }

        // Choose position of the recharge station of a randomly chosen district
        int[] positionChargeStation = new int[]{0,9};
        Random r = new Random();
        int x = positionChargeStation[r.nextInt(positionChargeStation.length)];
        int y = positionChargeStation[r.nextInt(positionChargeStation.length)];
        Coordinate coordinate = new Coordinate(x,y);
        taxi.setCoordinate(coordinate);

        // Set battery Level to 100
        taxi.setBatteryLevel(100);

        // Return to taxi the list of taxis already registered in the smart-city
        //taxi.setAllTaxi(Taxis.getInstance().getTaxisList());

        // Add taxi to list ➕
        taxiList.add(taxi);
        return true;
    }


    // Delete Taxi from the list
    public synchronized boolean delete(Integer taxiIdToDelete){

       Boolean inInList = isInList(taxiIdToDelete);

       if(inInList){
           taxiList.removeIf(taxi -> taxi.getId().equals(taxiIdToDelete));
           return true;
       }else {
           return false;
       }
    }


}
