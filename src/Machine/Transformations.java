package Machine;

import FactoryFloor.Tile;
import Utils.Orientation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static Utils.Orientation.*;


public interface Transformations {
    /*
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

    Orientation[] DIRECTIONS = {N, E, S, W};
    ThreadLocalRandom r = ThreadLocalRandom.current();

    ArrayDeque<Orientation> SQUARE = new ArrayDeque<>(
            Arrays.asList(N, E, S));

    ArrayDeque<Orientation> ELBOWR = new ArrayDeque<>(
            Arrays.asList(N, N, E));

    ArrayDeque<Orientation> ELBOWL = new ArrayDeque<>(
            Arrays.asList(N, N, W));

    ArrayDeque<Orientation> ROD = new ArrayDeque<>(
            Arrays.asList(N, N, N));

    ArrayDeque<Orientation> KINKR = new ArrayDeque<>(
            Arrays.asList(N, E, N));

    ArrayDeque<Orientation> KINKL = new ArrayDeque<>(
            Arrays.asList(N, W, N));

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

    static int getOrientationDifference(Orientation o1, Orientation o2) {
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

    // everything is in original orientation, just change from N to 'orientation'
    static ArrayDeque<Orientation> getSurroundingOrientations(Shapes shape, int id, Orientation orientation) {
        ArrayDeque<Orientation> orientations = null;
        switch (shape) {
            case Square:
                switch (id) {
                    case 0:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(S, W)));
                        break;
                    case 1:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(N, W)));
                        break;
                    case 2:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(N, E)));
                        break;
                    case 3:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(E, S)));
                        break;

                }
                break;
            case ElbowL:
                switch (id) {
                    case 0:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(E, S, W)));
                        break;
                    case 1:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(E, W)));
                        break;
                    case 2:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(N, E)));
                        break;
                    case 3:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(N, S, W)));
                        break;

                }
                break;
            case ElbowR:
                switch (id) {
                    case 0:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(E, S, W)));
                        break;
                    case 1:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(E, W)));
                        break;
                    case 2:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(N, W)));
                        break;
                    case 3:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(N, E, S)));
                        break;

                }
                break;
            case Rod:
                switch (id) {
                    case 0:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(E, S, W)));
                        break;
                    case 3:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(N, E, W)));
                        break;
                    default:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(E, W)));
                }
                break;
            case KinkL:
                switch (id) {
                    case 0:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(E, S, W)));
                        break;
                    case 1:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Collections.singletonList(E)));
                        break;
                    case 2:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Collections.singletonList(W)));
                        break;
                    case 3:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(N, E, W)));
                        break;

                }
                break;
            case KinkR:
                switch (id) {
                    case 0:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(E, S, W)));
                        break;
                    case 1:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Collections.singletonList(W)));
                        break;
                    case 2:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Collections.singletonList(E)));
                        break;
                    case 3:
                        orientations = new ArrayDeque<>(
                                new ArrayList<>(Arrays.asList(N, E, W)));
                        break;

                }
                break;
        }
        return rotateOrientations(orientations, orientation);
    }


}
