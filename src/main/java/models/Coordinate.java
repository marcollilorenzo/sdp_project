package models;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

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
        if(y <= 4){
            if(x <= 4){
                return 1;
            }else{
                return 2;
            }
        }else{
            if(x <= 4){
                return 3;
            }else{
                return 4;
            }
        }
    }

    @Override
    public String toString() {
        return
                x +
                "--" + y;
    }
}
