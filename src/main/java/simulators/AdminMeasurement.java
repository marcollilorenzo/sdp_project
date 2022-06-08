package simulators;

import java.util.ArrayList;
import java.util.List;

public class AdminMeasurement implements Buffer{

    public AdminMeasurement(){} // costruttore
    public ArrayList<Measurement> buffer = new ArrayList<>();

    @Override
    public synchronized void addMeasurement(Measurement m) {
        buffer.add(m);
        if (buffer.size() == 8){
            //System.out.println("Produttore - Notify"); // debug
            this.notify();
        }
    }

    @Override
    public List<Measurement> readAllAndClean() {
        return null;
    }
}
