package Machines;

import Utils.Orientation;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;


public interface Machines {
    /**
     * directions
     * [N, E, S, W]
     * <p>
     * square
     * [X][E]
     * [W][S]
     * <p>
     * elbowR
     *       [N]
     * [X][E][E]
     * <p>
     * elbowL
     * [X][E][E]
     *       [S]
     * <p>
     * rod
     * [X][E][E][E]
     * <p>
     * kinkR
     * [X]
     * [S][E]
     *    [S]
     * <p>
     * kinkL
     *    [X]
     * [W][S]
     * [S]
     */

    Orientation[] DIRECTIONS = {Orientation.N, Orientation.E, Orientation.S, Orientation.W};

    ArrayDeque<Orientation> SQUARE = new ArrayDeque<>(
            Arrays.asList(Orientation.E, Orientation.S, Orientation.W));

    ArrayDeque<Orientation> ELBOWR = new ArrayDeque<>(
            Arrays.asList(Orientation.E, Orientation.E, Orientation.N));

    ArrayDeque<Orientation> ELBOWL = new ArrayDeque<>(
            Arrays.asList(Orientation.E, Orientation.E, Orientation.S));

    ArrayDeque<Orientation> ROD = new ArrayDeque<>(
            Arrays.asList(Orientation.E, Orientation.E, Orientation.E));

    ArrayDeque<Orientation> KINKR = new ArrayDeque<>(
            Arrays.asList(Orientation.S, Orientation.E, Orientation.S));

    ArrayDeque<Orientation> KINKL = new ArrayDeque<>(
            Arrays.asList(Orientation.S, Orientation.W, Orientation.S));

    static Orientation getRotated(Orientation orientation, Rotations rotation) {
        int index = Arrays.asList(DIRECTIONS).indexOf(orientation);
        if (rotation == Rotations.CW) {
            index = (index + 1) % DIRECTIONS.length;
        } else {
            index = (index - 1) % DIRECTIONS.length;
        }
        return DIRECTIONS[index];
    }

    static Orientation getRandomOrient(){
        int index = new Random().nextInt(4);
        return DIRECTIONS[index];
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

    static ArrayDeque<Orientation> rotateOrientations(ArrayDeque<Orientation> orientations, Rotations rotation) {
        return orientations.stream().map(x -> getRotated(x, rotation))
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
