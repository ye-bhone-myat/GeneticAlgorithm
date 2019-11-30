package edu.oswego.csc375.Machine.Machines;

import edu.oswego.csc375.FactoryFloor.Tile;
import edu.oswego.csc375.Utils.Orientation;
import edu.oswego.csc375.Machine.Shapes;

import java.util.ArrayList;

public final class MachineFactory {
    public static AbstractMachine makeMachine(ArrayList<Tile> grids, Shapes shape, Orientation orientation){
        if (shape == Shapes.Square){
            return new Square(grids, shape, orientation);
        } else if (shape == Shapes.ElbowR){
            return new ElbowR(grids, shape, orientation);
        } else if (shape == Shapes.ElbowL){
            return new ElbowL(grids, shape, orientation);
        } else if (shape == Shapes.Rod){
            return new Rod(grids, shape, orientation);
        } else if (shape == Shapes.KinkR){
            return new KinkR(grids, shape, orientation);
        } else if (shape == Shapes.KinkL){
            return new KinkL(grids, shape, orientation);
        } else {
            return null;
        }
    }
}
