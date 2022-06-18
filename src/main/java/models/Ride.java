package models;

public class Ride {

    private int id;
    private Coordinate startPosition;
    private Coordinate endPosition;

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
        int rooting = (
                (this.endPosition.getX() - this.startPosition.getX())^2
                        +
                (this.endPosition.getY() - this.startPosition.getY())^2
        );

        double distance = Math.sqrt(rooting);
        return distance;
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
