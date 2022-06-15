package models;

import java.util.ArrayList;

public class RidesQueue {

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

    // add
    public synchronized void add(Ride r){
        ridesList.add(r);
        this.notify();
    }

    // remove
    public synchronized void remove(int id){
        for (Ride r: ridesList)
            if (r.getId() == id)
                ridesList.remove(r);
    }

    // get
    public synchronized ArrayList<Ride> getOrdersList(){
        return new ArrayList<>(ridesList);
    }
}
