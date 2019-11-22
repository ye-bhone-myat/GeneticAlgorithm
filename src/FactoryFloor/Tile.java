package FactoryFloor;

import Machines.Shapes;
import Utils.Orientation;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Tile implements Comparable{
    private Tile northTile, eastTile, southTile, westTile;
    private boolean occupied;
    private Shapes shape;
    private int x, y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        shape = null;
        northTile = null;
        eastTile = null;
        southTile = null;
        westTile = null;
        occupied = false;
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

    Tile getTile(Orientation direction){
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

    Shapes getShape(){
        return shape;
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

    ArrayList<Tile> place(ArrayDeque<Orientation> nextTiles, Shapes s){
        this.shape = s;
        this.occupied = true;
        if (!nextTiles.isEmpty()) {
            Orientation orientation = nextTiles.pop();
            if (orientation == Orientation.N) {
                ArrayList<Tile> lst = northTile.place(nextTiles, s);
                lst.add(0, this);
                return lst;
            } else if (orientation == Orientation.E) {
                ArrayList<Tile> lst = eastTile.place(nextTiles, s);
                lst.add(0, this);
                return lst;
            } else if (orientation == Orientation.S) {
                ArrayList<Tile> lst = southTile.place(nextTiles, s);
                lst.add(0, this);
                return lst;
            } else {
                ArrayList<Tile> lst = westTile.place(nextTiles, s);
                lst.add(0, this);
                return lst;
            }
        } else {
            ArrayList<Tile> lst = new ArrayList<>();
            lst.add(this);
            return lst;
        }
    }

    boolean isOccupied(){
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
