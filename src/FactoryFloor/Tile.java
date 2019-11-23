package FactoryFloor;

import Machines.Machine;
import Machines.Shapes;
import Utils.Orientation;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Tile implements Comparable{
    private Tile northTile, eastTile, southTile, westTile;
    private boolean occupied;
    private Machine machine;
    private int x, y;

    public Tile(int x, int y) {
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

    Tile getNeighbor(Orientation direction){
        if (direction == Orientation.N){
            return northTile;
        } else if (direction == Orientation.E){
            return eastTile;
        } else if (direction == Orientation.S){
            return southTile;
        } else if (direction == Orientation.W){
            return westTile;
        } else {
            return null;
        }
    }


    Tile getNeighbor(Orientation direction, int distance){
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

    Tile getNeighbor (ArrayDeque<Orientation> directions){
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

    public Machine getMachine(){
        return machine;
    }

    public void clear(){
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
                Machine m = new Machine(lst, s, o);
                lst.forEach(x -> x.machine = m);
            }
            return lst;
        } else {
            ArrayList<Tile> lst = new ArrayList<>();
            lst.add(this);
            return lst;
        }
    }

    public boolean isOccupied(){
        return occupied;
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
