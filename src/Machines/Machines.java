package Machines;

import Utils.Orientation;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public interface Machines {
    /**
     * directions
     * [N, E, S, W]
     * <p>
     * square
     * [N][E]
     * [X][S]
     * <p>
     * elbowR
     * [N][E]
     * [N]
     * [X]
     * <p>
     * elbowL
     * [S][N]
     *    [N]
     *    [X]
     * <p>
     * rod
     * [N]
     * [N]
     * [N]
     * [X]
     * <p>
     * kinkR
     *    [N]
     * [N][E]
     * [X]
     * <p>
     * kinkL
     * [N]
     * [W][N]
     *    [X]
     */

    Orientation[] DIRECTIONS = {Orientation.N, Orientation.E, Orientation.S, Orientation.W};
    ThreadLocalRandom r = ThreadLocalRandom.current();

    ArrayDeque<Orientation> SQUARE = new ArrayDeque<>(
            Arrays.asList(Orientation.N, Orientation.E, Orientation.S));

    ArrayDeque<Orientation> ELBOWR = new ArrayDeque<>(
            Arrays.asList(Orientation.N, Orientation.N, Orientation.E));

    ArrayDeque<Orientation> ELBOWL = new ArrayDeque<>(
            Arrays.asList(Orientation.N, Orientation.N, Orientation.W));

    ArrayDeque<Orientation> ROD = new ArrayDeque<>(
            Arrays.asList(Orientation.N, Orientation.N, Orientation.N));

    ArrayDeque<Orientation> KINKR = new ArrayDeque<>(
            Arrays.asList(Orientation.N, Orientation.E, Orientation.N));

    ArrayDeque<Orientation> KINKL = new ArrayDeque<>(
            Arrays.asList(Orientation.N, Orientation.W, Orientation.N));

    static Orientation randomOrientation() {
        return DIRECTIONS[r.nextInt(DIRECTIONS.length)];
    }

    static Orientation rotate(Orientation orientation, int rotation) {
        int index = Arrays.asList(DIRECTIONS).indexOf(orientation);
        index = (index + rotation) % DIRECTIONS.length;
        return DIRECTIONS[Math.abs(index)];
    }

    static ArrayDeque<Orientation> getOrientationArrayDeque(Shapes s) {
        ArrayDeque<Orientation> orientations = null;
        if (s == Shapes.Square) {
            orientations = SQUARE;
        } else if (s == Shapes.ElbowL) {
            orientations = ELBOWL;
        } else if (s == Shapes.ElbowR) {
            orientations = ELBOWR;
        } else if (s == Shapes.Rod) {
            orientations = ROD;
        } else if (s == Shapes.KinkL) {
            orientations = KINKL;
        } else if (s == Shapes.KinkR) {
            orientations = KINKR;
        }
        return orientations;
    }

    static int getOrientationDifference(Orientation o1, Orientation o2){
        int index1 = Arrays.asList(DIRECTIONS).indexOf(o1);
        int index2 = Arrays.asList(DIRECTIONS).indexOf(o2);
        return index2 - index1;
    }

    static ArrayDeque<Orientation> rotateOrientations(ArrayDeque<Orientation> orientations, Orientation direction) {
        Orientation leadOrientation = orientations.getFirst();
        int difference = getOrientationDifference(leadOrientation, direction);
        return orientations.stream().map(x -> rotate(x, difference))
                .collect(Collectors.toCollection(ArrayDeque::new));

    }

    static char getDisplayChar(Shapes s) {
        char x = ' ';
        if (s == Shapes.Square) {
            x = 'X';
        } else if (s == Shapes.ElbowL || s == Shapes.ElbowR) {
            x = '=';
        } else if (s == Shapes.Rod) {
            x = '#';
        } else if (s == Shapes.KinkL || s == Shapes.KinkR) {
            x = '%';
        }
        return x;
    }

}
