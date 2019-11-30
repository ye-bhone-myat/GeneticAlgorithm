package edu.oswego.csc375.Machine.Machines;

import edu.oswego.csc375.FactoryFloor.Tile;
import edu.oswego.csc375.Machine.Shapes;
import edu.oswego.csc375.Utils.Orientation;

import java.util.ArrayList;
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
