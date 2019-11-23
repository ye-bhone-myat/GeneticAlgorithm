package Machines;

import FactoryFloor.Tile;
import Utils.Orientation;

import java.util.ArrayList;

public class Machine implements Comparable{
    private ArrayList<Tile> grids;
    private Shapes shape;
    private Orientation orientation;

    public Machine(ArrayList<Tile> grids, Shapes shape, Orientation orientation){
        this.grids = grids;
        this.shape = shape;
        this.orientation = orientation;
    }

    public Orientation getOrientation(){
        return orientation;
    }

    public ArrayList<Tile> getGrids() {
        return grids;
    }

    public Shapes getShape() {
        return shape;
    }

    public Tile getLeadTile(){
        return grids.get(0);
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
        return "[" + grids.toString() + ", " + shape + ", " + orientation + "]";
    }
}
