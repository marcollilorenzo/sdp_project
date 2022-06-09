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

    @Override
    public String toString() {
        return "Ride{" +
                "id=" + id +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                '}';
    }
}
