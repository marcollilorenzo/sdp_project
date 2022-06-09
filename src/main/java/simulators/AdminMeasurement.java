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
            this.notify();
        }
    }

    @Override
    public synchronized List<Measurement> readAllAndClean() {
        ArrayList<Measurement> measurementsCopy = new ArrayList<>();

        // WAIT if BUFFER not have just 8 measurement
        while(buffer.size() < 8) {
            try {
                this.wait();
            } catch (InterruptedException e) { e.printStackTrace();  }
        }
        if(buffer.size() == 8){

            measurementsCopy = new ArrayList<>(buffer);

            for (int i = 0; i < 4; i++) // delete 50% of value for guarantee the correct overlapping of window
                buffer.remove(i);

        }
        return measurementsCopy;
    }
}
