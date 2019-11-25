package Machine.Machines;

import FactoryFloor.Tile;
import Machine.Shapes;
import Utils.Orientation;

import java.util.ArrayList;
import java.util.Objects;

class KinkL extends AbstractMachine {
    KinkL(ArrayList<Tile> grids, Shapes shape, Orientation orientation) {
        super(grids, shape, orientation);
    }


    @Override
    public int evaluate() {
        int score = 0;
        int nulls = countNulls();
        int nullScore = (nulls > 4)? 4:nulls;

        for (AbstractMachine m : getSurroundingMachineSet()){
            switch(m.shape){
                case KinkL:
                case KinkR:
                    score += 1;
                    break;
                case Square:
                case Rod:
                    score += 2;
                    break;
                case ElbowL:
                case ElbowR:
            }
        }
        return score - nullScore;
    }
}
