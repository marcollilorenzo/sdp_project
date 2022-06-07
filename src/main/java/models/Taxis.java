package models;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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

    private synchronized boolean isInList(Integer taxiId){
        Taxi taxiWithEqualId = taxiList.stream()
                .filter(a -> a.getId() == taxiId)
                .findAny()
                .orElse(null);
        // Filter return NULL if not find a taxi with same id
        if(taxiWithEqualId == null){
            return false;
        }
        return true;
    }

    // Get taxis list
    public synchronized List<Taxi> getTaxisList(){
        return this.taxiList;
    }

    // Add Taxi to list
    public synchronized boolean add(Taxi taxi){

        Boolean isInList = isInList(taxi.getId());
        if(isInList){
            return false;
        }
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
