package Machine.Machines;

import FactoryFloor.Tile;
import Utils.Orientation;
import Machine.Shapes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public abstract class AbstractMachine implements Comparable{
    ArrayList<Tile> grids;
    Shapes shape;
    Orientation orientation;

    public AbstractMachine(ArrayList<Tile> grids, Shapes shape, Orientation orientation){
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

    HashSet<AbstractMachine> getSurroundingMachineSet() {
        HashSet<AbstractMachine> machines = new HashSet<>();
        grids.forEach(tile -> {
            tile.getNeighbors().stream().filter(x -> x == null && grids.contains(x))
//                    .collect(HashSet::new);
                    .forEach(x -> machines.add(x.getMachine()));
        });
        return machines;
    }

    ArrayList<Tile> getSurroundingTiles(){
        return grids.stream().flatMap(tile -> tile.getNeighbors().stream())
                .filter(tile -> !grids.contains(tile))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public abstract int evaluate();

    @Override
    public int compareTo(Object o) {
        AbstractMachine other = (AbstractMachine) o;
        Tile thisTile = this.grids.get(0);
        Tile otherTile = other.grids.get(0);
        return thisTile.compareTo(otherTile);
    }

    @Override
    public String toString(){
        return "[" + grids.toString() + ", " + shape + ", " + orientation + "]";
    }
}
