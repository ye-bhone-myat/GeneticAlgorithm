import FactoryFloor.Floor;
import Machines.Machine;
import Machines.Shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {


    public static void main(String[] args){
        int roomWidth = 16;
        int roomHeight = 16;
        Floor floor1 = new Floor(roomWidth, roomHeight);
        Floor floor2 = new Floor(roomWidth, roomHeight);
        ArrayList<Shapes> shapes = new ArrayList<>(Arrays.asList(
                Shapes.Square
                , Shapes.ElbowL
                , Shapes.KinkL
                , Shapes.ElbowR
                , Shapes.KinkR
                , Shapes.Rod
        ));
        Random r = new Random();
        int max = 32 + r.nextInt(7);
        for (int i = 0; i < max; ++i){
            int tries = 0;
            Shapes shape = shapes.get(r.nextInt(shapes.size()));
            boolean placed = floor1.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
            while (!placed && tries < 10){
                placed = floor1.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
                ++ tries;
            }
            if (!placed){
                --i;
            }
        }
        max = 32 + r.nextInt(7);
        for (int i = 0; i < max; ++i){
            int tries = 0;
            Shapes shape = shapes.get(r.nextInt(shapes.size()));
            boolean placed = floor2.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
            while (!placed && tries < 10){
                placed = floor2.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
                ++ tries;
            }
            if (!placed){
                --i;
            }
        }
        System.out.println("Floor Plan [1]");
        floor1.display();
        System.out.printf("[%15s%15s\n", "==", "]");
        System.out.println("Floor Plan [2]");
        floor2.display();
        System.out.printf("[%15s%15s\n", "==", "]");
        System.out.println("Floor Plan [1] cut off at 128");
        int start = r.nextInt(128);
        int end = r.nextInt(128) + start;
        ArrayList<Machine> machines = floor1.removeMachines(start, end);
        floor1.display();
        System.out.println("Removed " + machines.size() + " machine(s)");
        System.out.printf("[%15s%15s\n", "==", "]");
        System.out.println("Floor Plan [2] cut off at 128");
        floor2.clearMachines(start, end);
        floor2.display();
        System.out.printf("[%15s%15s\n", "==", "]");
        System.out.println("Floor Plan [2] with added bits from [1]");
        int added = floor2.addMachines(machines);
        floor2.display();
        System.out.printf("[%15s%15s\n", "==", "]");
        System.out.println(floor2.getMachines().size() + " machines total, " + added + " machines added");


//        System.out.println(machines.size());
//        System.out.println(machines);

    }

}
