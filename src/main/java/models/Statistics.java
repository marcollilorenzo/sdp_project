package models;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
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
    private List<Statistic> statisticsList;

    private static final ObjectMapper mapper = new ObjectMapper();

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

    public ObjectNode getLastStatisticsByTaxiId(int taxiID, int last){
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

        for (Statistic lastStat: lastStatisticsOfTaxi) {
            kmAverage += lastStat.getKm();
            batteryAverage += lastStat.getBatteryLevel();
            pollutionAverage += lastStat.getAverageListPollution();
        }

        ObjectNode json = mapper.createObjectNode();
        json.put("kmAverage", kmAverage / lastStatisticsOfTaxi.size());
        json.put("batteryAverage", batteryAverage / lastStatisticsOfTaxi.size());
        json.put("pollutionAverage", pollutionAverage / lastStatisticsOfTaxi.size());
        json.put("numberRides", lastStatisticsOfTaxi.get(lastStatisticsOfTaxi.size()-1).getRide());

        return json;

    }

    public ObjectNode getLastStatisticsBeetweenTimestamp(long time1, long time2){
        System.out.println("time1: " + time1);
        List<Statistic> statistics = getStatisticsList();
        List<Statistic> statisticsBeetween = getStatisticsList();


        for (Statistic stat: statistics) {
            if (time1 < stat.getTimestamp() && stat.getTimestamp() < time2){
                statisticsBeetween.add(stat);
            }
        }

        float kmAverage = 0;
        float batteryAverage = 0;
        float pollutionAverage = 0;

        for (Statistic lastStat: statisticsBeetween) {
            kmAverage += lastStat.getKm();
            batteryAverage += lastStat.getBatteryLevel();
            pollutionAverage += lastStat.getAverageListPollution();
        }

        ObjectNode json = mapper.createObjectNode();
        json.put("kmAverage", kmAverage / statisticsBeetween.size());
        json.put("batteryAverage", batteryAverage / statisticsBeetween.size());
        json.put("pollutionAverage", pollutionAverage / statisticsBeetween.size());
        json.put("numberRides", statisticsBeetween.get(statisticsBeetween.size()-1).getRide());

        return json;

    }


}
