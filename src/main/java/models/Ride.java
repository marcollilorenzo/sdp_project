package models;

public class Ride {

    // info
    private int id;
    private Coordinate startPosition;
    private Coordinate endPosition;

    // Costruttore vuoto
    public Ride(){};

    public Ride(int id, Coordinate startPosition, Coordinate endPosition) {
        this.id = id;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coordinate getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Coordinate startPosition) {
        this.startPosition = startPosition;
    }

    public Coordinate getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Coordinate endPosition) {
        this.endPosition = endPosition;
    }

    public double getDistance(){

        return Math.sqrt(
                Math.pow(this.endPosition.getX() - this.startPosition.getX(), 2) +
                        Math.pow(this.endPosition.getY() - this.startPosition.getY(), 2));

    }

    @Override
    public String toString() {
        return "Ride{" +
                "id=" + id +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                '}';
    }
}
