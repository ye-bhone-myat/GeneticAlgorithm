package Machine.Machines;

import FactoryFloor.Tile;
import Machine.Shapes;
import Utils.Orientation;

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
