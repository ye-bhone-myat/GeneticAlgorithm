package edu.oswego.csc375.Machine.Machines;

import edu.oswego.csc375.FactoryFloor.Tile;
import edu.oswego.csc375.Machine.Shapes;
import edu.oswego.csc375.Utils.Orientation;

import java.util.ArrayList;
import java.util.Objects;

class Rod extends AbstractMachine {
    Rod(ArrayList<Tile> grids, Shapes shape, Orientation orientation) {
        super(grids, shape, orientation);
    }

    @Override
    public int evaluate() {
        int score = getSurroundingMachineSet().size();
        int nulls = countNulls();


        return score - nulls;
    }
}
