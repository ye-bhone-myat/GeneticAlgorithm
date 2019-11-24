package Machine.Machines;

import FactoryFloor.Tile;
import Machine.Shapes;
import Utils.Orientation;

import java.util.ArrayList;
import java.util.Objects;

class ElbowR extends AbstractMachine {
    ElbowR(ArrayList<Tile> grids, Shapes shape, Orientation orientation) {
        super(grids, shape, orientation);
    }

    @Override
    public int evaluate() {
        int score = 0;
        int nulls = (int) getSurroundingTiles().stream().filter(Objects::isNull).count();
        int nullScore = (nulls > 4)? 4:nulls;

        for (AbstractMachine m : getSurroundingMachineSet()){
            switch(m.shape){
                case ElbowR:
                    score += 2;
                    break;
                case ElbowL:
                    score -= 1;
                    break;
                case Square:
                case Rod:
                    score += 1;
                    break;
                case KinkL:
                case KinkR:
            }
        }
        return score + (2 * nullScore);
    }
}
