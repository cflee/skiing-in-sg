package net.cflee;

/**
 * Created by cflee on 30/1/15.
 */
public class Coordinate implements Comparable<Coordinate> {
    public int x;
    public int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /* Generated */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (x != that.x) return false;
        if (y != that.y) return false;

        return true;
    }

    /* Generated */
    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    /**
     * Arrange in ascending order by x coordinate, then y coordinate.
     */
    @Override
    public int compareTo(Coordinate o) {
        if (x != o.x) {
            return x - o.x;
        } else {
            return y - o.y;
        }
    }

    @Override
    public String toString() {
        return "Coordinate{" + x + "," + y + '}';
    }
}
