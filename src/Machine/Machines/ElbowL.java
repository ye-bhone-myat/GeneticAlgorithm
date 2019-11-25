package Machine.Machines;

import FactoryFloor.Tile;
import Machine.Shapes;
import Machine.Transformations;
import Utils.Orientation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/*
elbowL
[S][N]
   [N]
   [X]
 */

class ElbowL extends AbstractMachine {
    ElbowL(ArrayList<Tile> grids, Shapes shape, Orientation orientation) {
        super(grids, shape, orientation);
    }


    @Override
    public int evaluate() {
        int score = 0;
        int nulls = countNulls();
        int nullScore = (nulls > 4)? 4:nulls;

        for (AbstractMachine m : getSurroundingMachineSet()){
            switch(m.shape){
                case ElbowL:
                    score += 2;
                    break;
                case ElbowR:
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
        return score + (nullScore * 2);
    }


//    @Override
//    HashSet<AbstractMachine> getSurroundingMachineSet() {
//        HashSet<AbstractMachine> scoredMachines = new HashSet<>();
//        AtomicInteger score = new AtomicInteger();
//        for (int i = 0; i < grids.size(); ++i){
//            Tile t = grids.get(i);
//            ArrayDeque<Orientation> orientations = Transformations.getSurroundingOrientations(this.shape, i, this.orientation);
//            orientations.stream().map(x ->{
//                AbstractMachine m = t.getNeighbor(x).getMachine();
//                if (m != null){
//                    if (!scoredMachines.contains(m)) {
//                        switch (m.shape) {
//                            case Square:
//                                score.addAndGet(2);
//                                break;
//                                case
//                        }
//                    }
//                    scoredMachines.add(m);
//                }
//            });
//        }
//    }
}
