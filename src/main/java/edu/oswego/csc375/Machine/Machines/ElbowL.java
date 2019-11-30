package edu.oswego.csc375.Machine.Machines;

import edu.oswego.csc375.FactoryFloor.Tile;
import edu.oswego.csc375.Machine.Shapes;
import edu.oswego.csc375.Utils.Orientation;

import java.util.ArrayList;

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
