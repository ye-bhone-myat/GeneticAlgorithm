package Machines;

import FactoryFloor.Tile;

import java.util.ArrayList;

public class Machine implements Comparable{
    ArrayList<Tile> grids;
    Shapes shape;

    public Machine(ArrayList<Tile> grids, Shapes shape){
        this.grids = grids;
        this.shape = shape;
    }

    public ArrayList<Tile> getGrids() {
        return grids;
    }

    public Shapes getShape() {
        return shape;
    }

    @Override
    public int compareTo(Object o) {
        Machine other = (Machine) o;
        Tile thisTile = this.grids.get(0);
        Tile otherTile = other.grids.get(0);
        return thisTile.compareTo(otherTile);
    }

    @Override
    public String toString(){
        return "[" + grids.toString() + ", " + shape + "]";
    }
}
