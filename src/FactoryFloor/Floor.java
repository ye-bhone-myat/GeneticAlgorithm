package FactoryFloor;

import Machine.Machines.AbstractMachine;
import Utils.Orientation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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
    private final ReentrantLock lock;
    private boolean isSwapped;

    public Floor(int floorwidth, int floorheight) {
        this.height = floorheight;
        this.width = floorwidth;
        tiles = new Tile[floorheight][floorwidth];
        machines = new ArrayList<>();
        generateTiles();
        this.ID = IDGenerator.nextID();
        lock = new ReentrantLock();
        isSwapped = false;
    }

    public int getScore() {
        return score;
    }

    public int getMachineCount() {
        return machines.size();
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
        populate(32, 8);
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

    private void populate(int min, int max) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int count = min + r.nextInt(max);
        for (int i = 0; i < count; ++i) {
            Shapes shape = Transformations.getRandomShape();
            boolean placed = place(shape, r.nextInt(height), r.nextInt(width));
            if (!placed) {
                if (count < min) {
                    --i;
                }
            }
        }
        calculateScore();
    }

    public boolean place(Shapes shape, int x, int y) {
        ArrayDeque<Orientation> orientationArrayDeque = Transformations.getOrientationArrayDeque(shape);
        Orientation o = Transformations.randomOrientation();
        orientationArrayDeque = Transformations.rotateToOrientations(orientationArrayDeque, o);
        int i = 0;
        boolean placeable = false;
        while (i < 4 && !(placeable = tiles[y][x].placeable(orientationArrayDeque.clone()))) {
            ++i;
            o = Transformations.rotate(o, 1);
            orientationArrayDeque = Transformations.rotateToOrientations(orientationArrayDeque, o);
        }
//        ArrayList<Tile> addedShape;
        if (placeable) {
            tiles[y][x].place(orientationArrayDeque.clone(), shape, o);
//            AbstractMachine machine = new AbstractMachine(addedShape, shape);
            machines.add(tiles[y][x].getMachine());
        }
        return placeable;
    }

    public boolean place(Shapes shape, int x, int y, Orientation orientation) {
        ArrayDeque<Orientation> orientationArrayDeque = Transformations.getOrientationArrayDeque(shape);
        orientationArrayDeque = Transformations.rotateToOrientations(orientationArrayDeque, orientation);
        boolean placeable = tiles[y][x].placeable(orientationArrayDeque.clone());
        if (placeable) {
            tiles[y][x].place(orientationArrayDeque.clone(), shape, orientation);
//            AbstractMachine machine = new AbstractMachine(addedShape, shape);
            machines.add(tiles[y][x].getMachine());
        }
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

    public AbstractMachine removeMachine(int x, int y) {
        AbstractMachine tm = tiles[y][x].getMachine();
        tm.getGrids().forEach(Tile::clear);
        machines.remove(tm);
        return tm;
    }

    void swap(Floor f2) {

        /*
        if this ID > f2.getID,
            release this lock, call swap on f2
            if lock is held, unlock

        if this ID < f2.getID
            lock this, removeMachines()
            f2.replace()
            release this lock
         */

        ThreadLocalRandom r = ThreadLocalRandom.current();
        int bound = (height * width) / 2;
        int start = r.nextInt(bound);
        int end = r.nextInt(bound) + start + 1;
        double successChance = (this.score + f2.score)/2;
        if (successChance > 0 && r.nextDouble() > successChance) {
            try {
                lock.lock();
                ArrayList<AbstractMachine> machines1 = this.removeMachines(start, end);
                ArrayList<AbstractMachine> machines2 = f2.replace(machines1, start, end);
                if (!machines2.isEmpty()) {
                    addMachines(machines2, start, end);
                }
                this.isSwapped = true;
                int size = machines.size();
                if (size < 32) {
                    populate(32 - size, 8);
                }
            } finally {
                lock.unlock();
            }
        }

    }

    ArrayList<AbstractMachine> replace(ArrayList<AbstractMachine> incoming, int start, int end) {
        ArrayList<AbstractMachine> outgoing = new ArrayList<>();
        if (!incoming.isEmpty()) {
            try {
                lock.lock();
                outgoing = removeMachines(start, end);

                addMachines(incoming, start, end);
                this.isSwapped = true;
                int size = machines.size();
                if (size < 32) {
                    populate(32 - size, 8);
                }
            } finally {
                lock.unlock();
            }
        }
        return outgoing;
    }


    public void mutate() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int geneLength = r.nextInt((width * height) / 10) + 1;
        int start = r.nextInt((width * height) - geneLength);
        int end = start + geneLength;

        try {
            lock.lock();
            ArrayList<AbstractMachine> machines1 = this.removeMachines(start, end);

            int start2 = r.nextInt((width * height) - geneLength);
            int end2 = start2 + geneLength;
            while (start2 < end
                    && end2 > start) {
                // generate new values
                start2 = r.nextInt((width * height) - geneLength);
                end2 = start2 + geneLength;
            }
            ArrayList<AbstractMachine> machines2 = this.removeMachines(start2, end2);
            if (!machines1.isEmpty()) addMachines(machines1, start2, end2);
            if (!machines2.isEmpty()) addMachines(machines2, start, end);
            int size = machines.size();
            if (size < 32) {
                populate(32 - size, 8);
            }
        } finally {
            lock.unlock();
        }

    }

    private int[] convertToGridPoint(int index) {
        int[] grid = new int[2];
        grid[0] = Math.floorDiv(index, height);
        grid[1] = index - (height * grid[0]);
        return grid;
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
    private int addMachines(ArrayList<AbstractMachine> machineArrayList) {
        return (int) machineArrayList.stream().sorted((Comparator<AbstractMachine>) (m1, m2) -> m1.compareTo(m2))
                .map(machine ->
                        place(machine.getShape(),
                                machine.getLeadTile().getY(), machine.getLeadTile().getX(),
                                machine.getOrientation())
                ).filter(x -> x).count();
    }

    private int addMachines(ArrayList<AbstractMachine> machinesList, int start, int end) {
        ArrayList<Tile> subTiles = getTilesList(start, end);
        // calculate difference between the distance from head tile to tail tile in machineList,
        // and the start and end points
        int wiggleRoom = start + getFlatLength(machinesList.get(0).getLeadTile(),
                machinesList.get(machinesList.size() - 1).getLeadTile());
        int placementStart = start;
        int added = 0;
        for (AbstractMachine s : machinesList) {
            for (int i = placementStart; i < end; ++i) {
                Tile currentTile = subTiles.get(i - start);
                if (place(s.getShape(), currentTile.getY(), currentTile.getX(), s.getOrientation())) {
                    placementStart = i;
                    ++added;
                    break;
                } else {
                    if (placementStart == wiggleRoom) {
                        placementStart = i;
                        break;
                    }
                }

            }
        }
        return added;
    }

    private int getFlatLength(Tile x, Tile y) {
        int yDiff = Math.abs(y.getY() - x.getY());
        int xDiff = Math.abs(y.getX() - x.getX());
        return (yDiff * height) + xDiff;
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