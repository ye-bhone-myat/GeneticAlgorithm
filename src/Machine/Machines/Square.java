package Machine.Machines;

import FactoryFloor.Tile;
import Machine.Shapes;
import Utils.Orientation;

import java.util.ArrayList;

class Square extends AbstractMachine {
    Square(ArrayList<Tile> grids, Shapes shape, Orientation orientation) {
        super(grids, shape, orientation);
    }


    @Override
    public int evaluate() {
        return countNotNulls();
    }
}
