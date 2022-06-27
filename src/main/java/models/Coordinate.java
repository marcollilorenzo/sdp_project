package models;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Coordinate {

    private int x;
    private int y;

    // empty costructor
    public Coordinate(){}

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
        //System.out.println("Point.java "+x);//debug
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDistrict(){
        if(x <= 4){
            if(y <= 4){
                return 1;
            }else{
                return 2;
            }
        }else{
            if(y <= 4){
                return 4;
            }else{
                return 3;
            }
        }
    }

    public ArrayList<Integer> getStationCoordinateByDistrict() {
        int dist = getDistrict();

        ArrayList<Integer> coordinateArray = new ArrayList<Integer>();

        if(dist == 1){
            coordinateArray.add(0);
            coordinateArray.add(0);

        }

        if(dist == 2){
            coordinateArray.add(0);
            coordinateArray.add(9);
        }

        if(dist == 3){
            coordinateArray.add(9);
            coordinateArray.add(0);
        }

        if(dist == 4){
            coordinateArray.add(9);
            coordinateArray.add(9);
        }

        return coordinateArray;


    }

    @Override
    public String toString() {
        return
                x +
                "--" + y;
    }
}
