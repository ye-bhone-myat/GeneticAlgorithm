import FactoryFloor.Floor;
import Machines.Machine;
import Machines.Shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Main {


    public static void main(String[] args){
        int roomWidth = 16;
        int roomHeight = 16;
        Floor floor = new Floor(roomWidth, roomHeight);
        ArrayList<Shapes> shapes = new ArrayList<>(Arrays.asList(
                Shapes.Square
                , Shapes.ElbowL
                , Shapes.KinkL
                , Shapes.ElbowR
                , Shapes.KinkR
                , Shapes.Rod
        ));
        Random r = new Random();
        for (int i = 0; i < 36; ++i){
            int tries = 0;
            Shapes shape = shapes.get(r.nextInt(shapes.size()));
            boolean placed = floor.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
            while (!placed && tries < 10){
                placed = floor.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
                ++ tries;
            }
            if (!placed){
                --i;
            }
        }
//        shapes.forEach(x -> {
//            int tries = 0;
//            boolean placed = floor.place(x, r.nextInt(roomHeight), r.nextInt(roomWidth));
//            while (!placed && tries < 10){
//                placed = floor.place(x, r.nextInt(roomHeight), r.nextInt(roomWidth));
//                ++ tries;
//            }
//        });
//        shapes.forEach(x -> floor.place(x, 2, 2));
        floor.display();
        ArrayList<Machine> machines = floor.getMachines();
        machines.sort(Comparator.naturalOrder());
        System.out.println(machines);
    }

}
