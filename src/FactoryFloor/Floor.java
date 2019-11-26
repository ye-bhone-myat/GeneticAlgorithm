package FactoryFloor;

import Machine.Machines.AbstractMachine;
import Utils.Orientation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

import Machine.*;

public class Floor implements Comparable {
    private int height, width;
    private Tile[][] tiles;
    private ArrayList<AbstractMachine> machines;
    private final int ID;
    private int score;
    ReentrantLock lock;
    private boolean isSwapped;
    private ThreadLocalRandom r;

    public Floor(int floorwidth, int floorlength) {
        this.height = floorlength;
        this.width = floorwidth;
        tiles = new Tile[floorlength][floorwidth];
        machines = new ArrayList<>();
        generateTiles();
        this.ID = IDGenerator.nextID();
        this.score = 0;
        lock = new ReentrantLock();
        isSwapped = false;
    }

    public int getScore() {
        return score;
    }

    public void resetSwapped() {
        isSwapped = false;
    }

    public int getID() {
        return ID;
    }

    private void generateTiles() {
        for (int j = 0; j < tiles.length; ++j) {
            for (int i = 0; i < tiles[0].length; ++i) {
                tiles[j][i] = new Tile(i, j);
            }
        }
        assignNeighbors();
    }

    private void assignNeighbors() {
        for (int i = 0; i < tiles.length; ++i) {
            for (int j = 0; j < tiles[0].length; ++j) {
                if (i == 0) {
                    if (j == 0) {
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                    } else if (j == tiles[0].length - 1) {
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                    } else {
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                    }
                } else if (i == tiles.length - 1) {
                    if (j == 0) {
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                    } else if (j == tiles[0].length - 1) {
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                    } else {
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                    }
                } else {
                    if (j == 0) {
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                    } else if (j == tiles[0].length - 1) {
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                    } else {
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                    }
                }
            }
        }
    }

    public boolean place(Shapes shape, int x, int y) {
        ArrayDeque<Orientation> orientationArrayDeque = Transformations.getOrientationArrayDeque(shape);
        Orientation o = Transformations.randomOrientation();
        orientationArrayDeque = Transformations.rotateOrientations(orientationArrayDeque, o);
        int i = 0;
        boolean placeable = false;
        while (i < 4 && !(placeable = tiles[y][x].placeable(orientationArrayDeque.clone()))) {
            ++i;
            o = Transformations.randomOrientation();
            orientationArrayDeque = Transformations.rotateOrientations(orientationArrayDeque, o);
        }
//        ArrayList<Tile> addedShape;
        if (placeable) {
            tiles[y][x].place(orientationArrayDeque.clone(), shape, o);
//            AbstractMachine machine = new AbstractMachine(addedShape, shape);
            machines.add(tiles[y][x].getMachine());
        }
        calculateScore();
        return placeable;
    }

    public boolean place(Shapes shape, int x, int y, Orientation orientation) {
        ArrayDeque<Orientation> orientationArrayDeque = Transformations.getOrientationArrayDeque(shape);
        orientationArrayDeque = Transformations.rotateOrientations(orientationArrayDeque, orientation);
        boolean placeable = tiles[y][x].placeable(orientationArrayDeque.clone());
        if (placeable) {
            tiles[y][x].place(orientationArrayDeque.clone(), shape, orientation);
//            AbstractMachine machine = new AbstractMachine(addedShape, shape);
            machines.add(tiles[y][x].getMachine());
        }
        calculateScore();
        return placeable;
    }

    public ArrayList<AbstractMachine> getMachines() {
        machines.sort(naturalOrder());
        return machines;
    }

    public ArrayList<Tile> getTilesList() {
        ArrayList<Tile> tilesArrayList = new ArrayList<>();
        for (Tile[] tile : tiles) {
            tilesArrayList.addAll(Arrays.asList(tile).subList(0, tiles[0].length));
        }
        return tilesArrayList;
    }

    public ArrayList<Tile> getTilesList(int start, int end) {
        return new ArrayList<>(getTilesList().subList(start, end));
    }

    public void clearMachines(int start, int end) {
        ArrayList<Tile> tilesArrayList = getTilesList(start, end);
        tilesArrayList.forEach(tile -> {
            if (tile.isOccupied()) {
                AbstractMachine tm = tile.getMachine();
                tm.getGrids().forEach(Tile::clear);
                machines.remove(tm);
            }
        });
    }

    public ArrayList<AbstractMachine> removeMachines(int start, int end) {
        ArrayList<Tile> tilesArrayList = getTilesList(start, end);
        return tilesArrayList.stream().filter(Tile::isOccupied).map(tile -> {
            AbstractMachine tm = tile.getMachine();
            tm.getGrids().forEach(Tile::clear);
            machines.remove(tm);
            return tm;
        }).sorted((Comparator<AbstractMachine>) (m1, m2) -> m1.compareTo(m2))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void swap(Floor f2) {
        r = ThreadLocalRandom.current();
        int bound = (height * width) / 2;
        int start = r.nextInt(bound);
        int end = r.nextInt(bound) + start + 1;
        ArrayList<AbstractMachine> machines1 = this.removeMachines(start, end);
        ArrayList<AbstractMachine> machines2 = f2.removeMachines(start, end);
        this.clearMachines(start, end);
        f2.clearMachines(start, end);
        this.addMachines(machines2);
        f2.addMachines(machines1);
        this.isSwapped = true;
        f2.isSwapped = true;
    }

    public boolean isSwapped() {
        return isSwapped;
    }

    /**
     * Attempts to add all the AbstractMachine objs in
     * machineArrayList
     *
     * @param machineArrayList list of AbstractMachine objs to be added
     * @return # of AbstractMachine objs successfully added
     */
    public int addMachines(ArrayList<AbstractMachine> machineArrayList) {
        return (int) machineArrayList.stream().sorted((Comparator<AbstractMachine>) (m1, m2) -> m1.compareTo(m2))
                .map(machine ->
                        place(machine.getShape(),
                                machine.getLeadTile().getX(), machine.getLeadTile().getY(),
                                machine.getOrientation())
                ).filter(x -> x).count();
    }

    public void display() {
        System.out.print(" ");
        for (int i = 0; i < tiles.length; ++i) {
            System.out.printf("%3d", i);
        }
        System.out.println();
        for (int i = 0; i < tiles.length; ++i) {
            System.out.printf("%2d", i);
            for (int j = 0; j < tiles[i].length; ++j) {
                char x = (tiles[i][j].isOccupied()) ? Transformations.getDisplayChar(tiles[i][j].getMachine().getShape()) : ' ';
                System.out.print(" " + x + " ");
            }
            System.out.println();
        }
    }

    public int calculateScore() {
        int score = 0;
        for (AbstractMachine machine : machines) {
            if (machine != null)
                score += machine.evaluate();
        }
        score += machines.size();
        this.score = score;
        return score;
    }

    @Override
    public int compareTo(Object o) {
        Floor other = (Floor) o;
        return other.score - this.score;
    }

}