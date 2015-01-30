package net.cflee;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
    Objective: longest descending path, but break tie by drop size

    maximum possible length is limited by the starting height,
    as only possible to go to next cell that is LESS in any
    direction.
    e.g. starting height = 9, can only have up to 9 length.
    so if I've found a 7 length, no point checking <7 height starting points

    so we can start from the higher numbers down!
 */

public class Ski {
    int height;
    int width;
    int[][] map;
    // height (from high to low) => Set of Coordinates
    Map<Integer, Set<Coordinate>> coordinatesByHeight;
    int bestLength;
    int bestDrop;
    int iterationCount;

    public static void main(String[] args) throws FileNotFoundException {
        Ski ski = new Ski();

        long startTime = System.currentTimeMillis();
        ski.run("map.txt");
        System.out.println("Runtime: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public Ski() {
        // initialise the TreeMap with a reversed comparator
        // so that can just use normal Map.entrySet() later instead of the
        // descending variant on NavigableMap
        coordinatesByHeight = new TreeMap<>(Collections.reverseOrder());
    }

    void run(String filename) {
        // load data
        try {
            loadData(filename);
        } catch (FileNotFoundException e) {
            System.out.println("Bailing - can't find input file");
        } catch (NumberFormatException e) {
            System.out.println("Bailing - expected number but got something else");
        }

        // iterate over all starting heights, descending
        for (Map.Entry<Integer, Set<Coordinate>> entry : coordinatesByHeight.entrySet()) {
            // pull out K,V from Entry to make it clearer what it is
            int height = entry.getKey();
            Set<Coordinate> coordinates = entry.getValue();

            // terminate early: don't examine smaller heights than the current
            // best length
            if (height < bestLength) {
                break;
            }

            // iterate through each starting point for current height
            for (Coordinate c : coordinates) {
                search(c);
            }
        }

        // output results
        System.out.println("length=" + bestLength + ", drop=" + bestDrop);
        System.out.println("Path chains examined: " + iterationCount);
    }

    void loadData(String filename) throws FileNotFoundException {
        try (Scanner in = new Scanner(new File(filename))) {
            // obtain dimensions and instantiate data array
            String firstLine = in.nextLine().trim();
            String[] dimensions = firstLine.split(" ");

            height = Integer.parseInt(dimensions[0]);
            width = Integer.parseInt(dimensions[1]);
            map = new int[height][width];

            // retrieve remaining lines and fill up data array
            for (int x = 0; x < height; x++) {
                String line = in.nextLine().trim();
                String[] mapLine = line.split(" ");

                for (int y = 0; y < width; y++) {
                    map[x][y] = Integer.parseInt(mapLine[y]);
                    addCoordinateToGroupedHeightMap(x, y);
                }
            }
        }
    }

    // add this Coordinate to the coordinatesByHeight map
    // creating a Set if necessary
    void addCoordinateToGroupedHeightMap(int x, int y) {
        Set<Coordinate> coordinates = coordinatesByHeight.get(map[x][y]);
        if (coordinates == null) {
            coordinates = new TreeSet<>();
            coordinatesByHeight.put(map[x][y], coordinates);
        }
        coordinates.add(new Coordinate(x, y));
    }

    // initiate recursion
    void search(Coordinate startPoint) {
        List<Coordinate> path = new ArrayList<>();
        path.add(startPoint);

        searchAround(path);
    }

    // recursive method
    void searchAround(List<Coordinate> path) {
        iterationCount++;

        Coordinate tail = path.get(path.size() - 1);
        int tailHeight = map[tail.x][tail.y];
        boolean isDeadEnd = true; // flag to determine if this is end of chain

        // for top, right, bottom, left
        // foreach validate that in-bounds, then lower than current,
        // then is-not already in path
        // if ok, clone List and add new Coordinate, then recurse to navigate in
        //
        // top: x-1,y
        if (tail.x - 1 >= 0
                && map[tail.x - 1][tail.y] < tailHeight
                && !path.contains(new Coordinate(tail.x - 1, tail.y))) {
            List<Coordinate> nextPath = new ArrayList<>(path);
            nextPath.add(new Coordinate(tail.x - 1, tail.y));
            searchAround(nextPath);
            isDeadEnd = false;
        }

        // right: x,y+1
        if (tail.y + 1 < width
                && map[tail.x][tail.y + 1] < tailHeight
                && !path.contains(new Coordinate(tail.x, tail.y + 1))) {
            List<Coordinate> nextPath = new ArrayList<>(path);
            nextPath.add(new Coordinate(tail.x, tail.y + 1));
            searchAround(nextPath);
            isDeadEnd = false;
        }

        // bottom: x+1,y
        if (tail.x + 1 < height
                && map[tail.x + 1][tail.y] < tailHeight
                && !path.contains(new Coordinate(tail.x + 1, tail.y))) {
            List<Coordinate> nextPath = new ArrayList<>(path);
            nextPath.add(new Coordinate(tail.x + 1, tail.y));
            searchAround(nextPath);
            isDeadEnd = false;
        }

        // left: x,y-1
        if (tail.y - 1 >= 0
                && map[tail.x][tail.y - 1] < tailHeight
                && !path.contains(new Coordinate(tail.x, tail.y - 1))) {
            List<Coordinate> nextPath = new ArrayList<>(path);
            nextPath.add(new Coordinate(tail.x, tail.y - 1));
            searchAround(nextPath);
            isDeadEnd = false;
        }

        // terminating condition: if all 4 directions are not OK then calculate
        // the length and drop, and update instance variable if necessary
        if (isDeadEnd) {
            // calculate this endpoint's length and drop
            int length = path.size();
            Coordinate start = path.get(0);
            Coordinate end = path.get(path.size() - 1);
            int drop = map[start.x][start.y] - map[end.x][end.y];

            // update length and drop if necessary
            if (length > bestLength || (length == bestLength && drop > bestDrop)) {
                bestLength = length;
                bestDrop = drop;
            }
        }
    }
}
