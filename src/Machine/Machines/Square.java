package Machine.Machines;

import FactoryFloor.Tile;
import Machine.Shapes;
import Utils.Orientation;

import java.util.ArrayList;
import java.util.Objects;

class Square extends AbstractMachine {
    Square(ArrayList<Tile> grids, Shapes shape, Orientation orientation) {
        super(grids, shape, orientation);
    }


    @Override
    public int evaluate() {
        return (int) (2 * getSurroundingTiles().stream().filter(Objects::isNull).count());
    }
}
