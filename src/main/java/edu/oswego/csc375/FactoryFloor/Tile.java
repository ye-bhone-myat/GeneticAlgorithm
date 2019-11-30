package edu.oswego.csc375.FactoryFloor;

import edu.oswego.csc375.Machine.Machines.AbstractMachine;
import edu.oswego.csc375.Machine.Machines.MachineFactory;
import edu.oswego.csc375.Machine.Shapes;

import java.util.*;

import edu.oswego.csc375.Utils.Orientation;

public class Tile implements Comparable{
    private Tile northTile, eastTile, southTile, westTile;
    private boolean occupied;
    private AbstractMachine machine;
    private int x, y;

    Tile(int x, int y) {
        this.x = x;
        this.y = y;
        machine = null;
        northTile = null;
        eastTile = null;
        southTile = null;
        westTile = null;
        occupied = false;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    void setTile(Orientation direction, Tile t){
        if (direction == Orientation.N){
            northTile = t;
        } else if (direction == Orientation.E){
            eastTile = t;
        } else if (direction == Orientation.S){
            southTile = t;
        } else if (direction == Orientation.W){
            westTile = t;
        }
    }

    public Tile getNeighbor(Orientation direction){
        if (direction == Orientation.N){
            return northTile;
        } else if (direction == Orientation.E){
            return eastTile;
        } else if (direction == Orientation.S){
            return southTile;
        } else if (direction == Orientation.W){
            return westTile;
        }
        return null;
    }


    private Tile getNeighbor(Orientation direction, int distance){
        if (distance > 0) {
            if (direction == Orientation.N) {
                distance --;
                return (northTile == null)? null : northTile.getNeighbor(direction, distance);
            } else if (direction == Orientation.E) {
                distance --;
                return (eastTile == null)? null : eastTile.getNeighbor(direction, distance);
            } else if (direction == Orientation.S) {
                distance --;
                return (southTile == null)? null : southTile.getNeighbor(direction, distance);
            } else if (direction == Orientation.W) {
                distance --;
                return (westTile == null)? null : westTile.getNeighbor(direction, distance);
            } else {
                return null;
            }
        } else {
            return this;
        }
    }

    private Tile getNeighbor(ArrayDeque<Orientation> directions){
        if (!directions.isEmpty()) {
            Orientation direction = directions.pop();
            if (direction == Orientation.N) {
                return (northTile == null)? null : northTile.getNeighbor(directions);
            } else if (direction == Orientation.E) {
                return (eastTile == null)? null : eastTile.getNeighbor(directions);
            } else if (direction == Orientation.S) {
                return (southTile == null)? null : southTile.getNeighbor(directions);
            } else if (direction == Orientation.W) {
                return (westTile == null)? null : westTile.getNeighbor(directions);
            } else {
                return null;
            }
        } else {
            return this;
        }
    }

    public ArrayList<Tile> getNeighbors(){
        return new ArrayList<>(Arrays.asList(northTile, eastTile, southTile, westTile));
    }

    public AbstractMachine getMachine(){
        return machine;
    }

    void clear(){
        this.occupied = false;
        this.machine = null;
    }

    boolean placeable(ArrayDeque<Orientation> nextTiles) {
        if (nextTiles.isEmpty()) {
            return !occupied;
        } else {
            Orientation orientation = nextTiles.pop();
            if (orientation == Orientation.N) {
                if (northTile != null) {
                    return !occupied && northTile.placeable(nextTiles);
                } else {
                    return false;
                }
            } else if (orientation == Orientation.E) {
                if (eastTile != null) {
                    return !occupied && eastTile.placeable(nextTiles);
                } else {
                    return false;
                }
            } else if (orientation == Orientation.S) {
                if (southTile != null) {
                    return !occupied && southTile.placeable(nextTiles);
                } else {
                    return false;
                }
            } else if (orientation == Orientation.W) {
                if (westTile != null) {
                    return !occupied && westTile.placeable(nextTiles);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    ArrayList<Tile> place(ArrayDeque<Orientation> nextTiles, Shapes s, Orientation o){
//        this.shape = s;
        this.occupied = true;
        if (!nextTiles.isEmpty()) {
            Orientation orientation = nextTiles.pop();
            ArrayList<Tile> lst;
            if (orientation == Orientation.N) {
                lst = northTile.place(nextTiles, s, o);
            } else if (orientation == Orientation.E) {
                lst = eastTile.place(nextTiles, s, o);
            } else if (orientation == Orientation.S) {
                lst = southTile.place(nextTiles, s, o);
            } else {
                lst = westTile.place(nextTiles, s, o);
            }
            lst.add(0, this);
            if (lst.size() == 4){
                AbstractMachine m = MachineFactory.makeMachine(lst, s, o);
                lst.forEach(x -> x.machine = m);
            }
            return lst;
        } else {
            ArrayList<Tile> lst = new ArrayList<>();
            lst.add(this);
            return lst;
        }
    }

    boolean isOccupied(){
        return occupied;
    }

    public Tile getClosestTile(ArrayList<Tile> tiles){
        HashMap<Double, Tile> distances = new HashMap<>();
        ArrayList<Double> keys = new ArrayList<>();
        tiles.forEach(x -> {
            Double d = getEuclideanDistance(x);
            keys.add(d);
            distances.put(d, x);
        });
        keys.sort(Comparator.naturalOrder());
        return distances.get(keys.get(0));
    }

    private double getEuclideanDistance(Tile t){
        return Math.sqrt(Math.pow(t.x - x, 2) + Math.pow(t.y - y, 2));
    }

    @Override
    public int compareTo(Object o) {
        Tile other = (Tile) o;
        if (this.y < other.y) {
            return -1;
        } else if (this.y > other.y){
            return 1;
        } else {
            if (this.x < other.x){
                return -1;
            } else if (this.x > other.x){
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public String toString(){
        return "[" + x + ", " + y + "]";
    }


    /**
     * boolean placeable (ArrayDeque directions)
     * checks if self is occupied
     *  - if not occupied, pops a direction from directions
     *      - calls placeable with popped directions on Tile lying towards direction
     *
     */

}
