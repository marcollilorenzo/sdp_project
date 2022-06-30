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
    private ArrayList<Ride> allRides;
    private ArrayList<Ride> pendingRides;


    private static RidesQueue instance;
    private RidesQueue(){
        allRides = new ArrayList<Ride>();
        pendingRides = new ArrayList<Ride>();
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
            allRides.add(r);
            this.notify();
            return true;
        }


    }

    // add pending
    public synchronized boolean addPending(Ride r){

        Boolean isInList = isInList(r.getId());
        if(isInList){
            return false;
        } else {
            pendingRides.add(r);
            return true;
        }


    }

    // remove
    public synchronized void remove(int id){
        for (Ride r: allRides)
            if (r.getId() == id)
                allRides.remove(r);
    }

    // get
    public synchronized ArrayList<Ride> getRidesList(){
        return new ArrayList<>(allRides);
    }

    // get pending
    public ArrayList<Ride> getPendingRides() {
        return pendingRides;
    }

    // remove pending
    public synchronized void removePendingRides(int rideID){
        pendingRides.removeIf((ride -> ride.getId() == rideID));
    }

    // take ride from queue
    public Ride take(){

        Ride r;

        if(pendingRides.size()>0){
            r = pendingRides.get(0);
            pendingRides.remove(0);
        }else{
            return null;
        }

        return r;

    }

    // take ride from queue BY DISTRICT
    public Ride takeByDistict(int district){

        Ride r = null;

        if(pendingRides.size()>0){
            for (Ride ride: pendingRides) {
                if(ride.getStartPosition().getDistrict() == district){
                    pendingRides.remove(ride);
                    r = ride;
                    break;
                }
            }
        }else{
            return null;
        }
        return r;

    }

}
