package models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RidesQueue {

    @XmlElement(name="rides")

    // fields
    private ArrayList<Ride> ridesList;
    private static RidesQueue instance;
    private RidesQueue(){
        ridesList = new ArrayList<Ride>();
    }

    // singleton
    public synchronized static RidesQueue getInstance(){
        if (instance ==null)
            instance = new RidesQueue();
        return instance;
    }


    // Check if ad ID in the list
    private synchronized boolean isInList(int rideId){
        List<Ride> rideShallow = getRidesList(); // shallow copy
        Ride rideWithEqualId = rideShallow.stream()
                .filter(a -> a.getId() == rideId)
                .findAny()
                .orElse(null);
        // Filter return NULL if not find a taxi with same id
        if(rideWithEqualId == null){
            return false;
        }
        return true;
    }

    // add
    public synchronized boolean add(Ride r){

        Boolean isInList = isInList(r.getId());
        if(isInList){
            return false;
        } else {
            ridesList.add(r);
            this.notify();
            return true;
        }


    }

    // remove
    public synchronized void remove(int id){
        for (Ride r: ridesList)
            if (r.getId() == id)
                ridesList.remove(r);
    }

    // get
    public synchronized ArrayList<Ride> getRidesList(){
        return new ArrayList<>(ridesList);
    }
}
