package models;

import simulators.Measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Statistics {

    @XmlElement(name="stat")
    private static Statistics instance;

   // @XmlElement(name = "pollution")
    private List<Statistic> statisticsList;

    // Constructor
    private Statistics() {
        statisticsList = new ArrayList<Statistic>();
    }

    // Singleton
    public synchronized static Statistics getInstance(){
        if(instance==null)
            instance = new Statistics();
        return instance;
    }

    // Statistic list - GET - ADD
    public synchronized List<Statistic> getStatisticsList(){
        return new ArrayList<>(statisticsList);
    }
    public synchronized void addStatistic(Statistic s){
        statisticsList.add(s);
    }

    public int getSizeLastStatisticsByTaxiId(int taxiID){
        List<Statistic> statistics = getStatisticsList();
        List<Statistic> statisticsOfTaxi = new ArrayList<Statistic>();
        for (Statistic stat: statistics) {
            if (stat.getTaxiID() == taxiID){
                statisticsOfTaxi.add(stat);
            }
        }
        return statisticsOfTaxi.size();
    }

    public ArrayList<Float> getLastStatisticsByTaxiId(int taxiID, int last){
        List<Statistic> statistics = getStatisticsList();
        List<Statistic> statisticsOfTaxi = new ArrayList<Statistic>();
        List<Statistic> lastStatisticsOfTaxi = new ArrayList<Statistic>();

        for (Statistic stat: statistics) {
            if (stat.getTaxiID() == taxiID){
                statisticsOfTaxi.add(stat);
            }
        }

        lastStatisticsOfTaxi = statisticsOfTaxi.subList(statisticsOfTaxi.size() - last, statisticsOfTaxi.size());

        float kmAverage = 0;
        float batteryAverage = 0;
        float pollutionAverage = 0;
        float numberRides = 0;

        for (Statistic lastStat: lastStatisticsOfTaxi) {
            kmAverage += lastStat.getKm();
            batteryAverage += lastStat.getBatteryLevel();
            numberRides += lastStat.getRide();

            float singleAveragePollution = 0;

            for (Double m: lastStat.getAverageList()) { // media pollution della singola statistica
                singleAveragePollution += m;
            }

            pollutionAverage += singleAveragePollution/lastStat.getAverageList().size();

        }

        ArrayList<Float> result = new ArrayList<>();
        result.add(kmAverage / lastStatisticsOfTaxi.size());
        result.add(batteryAverage / lastStatisticsOfTaxi.size());
        result.add(pollutionAverage / lastStatisticsOfTaxi.size());
        result.add(numberRides);

        return result;

    }


}
