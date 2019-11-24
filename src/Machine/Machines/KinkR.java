package Machine.Machines;

import FactoryFloor.Tile;
import Machine.Shapes;
import Utils.Orientation;

import java.util.ArrayList;
import java.util.Objects;

class KinkR extends AbstractMachine {
    KinkR(ArrayList<Tile> grids, Shapes shape, Orientation orientation) {
        super(grids, shape, orientation);
    }

    @Override
    public int evaluate() {
        int score = 0;
        int nulls = (int) getSurroundingTiles().stream().filter(Objects::isNull).count();
        int nullScore = (nulls > 4)? 4:nulls;

        for (AbstractMachine m : getSurroundingMachineSet()){
            switch(m.shape){
                case KinkR:
                    score -= 2;
                    break;
                case Square:
                case Rod:
                    score += 2;
                    break;
                case ElbowL:
                case ElbowR:
                case KinkL:
            }
        }
        return score - nullScore;
    }
}
