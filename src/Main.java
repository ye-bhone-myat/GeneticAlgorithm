import FactoryFloor.Floor;
import Machine.Machines.AbstractMachine;
import Machine.Shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {


    public static void main(String[] args){
        int roomWidth = 6;
        int roomHeight = 6;
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
//        int max = 32 + r.nextInt(7);
        int max = 1;
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
//        max = 32 + r.nextInt(7);
        max = 1;
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
        System.out.println("Score: " + floor1.calculateScore());
        System.out.printf("[%15s%15s\n", "==", "]");
        System.out.println("Floor Plan [2]");
        floor2.display();
        System.out.println("Score: " + floor2.calculateScore());
        System.out.printf("[%15s%15s\n", "==", "]");
        int start = r.nextInt((roomHeight*roomWidth)/2);
        int end = r.nextInt(start) + start;
        System.out.println("Floor Plan [1] cut off from " + start + " to " + end + ".");
        ArrayList<AbstractMachine> machines = floor1.removeMachines(start, end);
        floor1.display();
        System.out.println("Score: " + floor1.calculateScore());
        System.out.println("Removed " + machines.size() + " machine(s)");
        System.out.printf("[%15s%15s\n", "==", "]");
        System.out.println("Floor Plan [2] cut off  from " + start + " to " + end + ".");
        floor2.clearMachines(start, end);
        floor2.display();
        System.out.println("Score: " + floor2.calculateScore());
        System.out.printf("[%15s%15s\n", "==", "]");
        System.out.println("Floor Plan [2] with added bits from [1]");
        int added = floor2.addMachines(machines);
        floor2.display();
        System.out.println("Score: " + floor2.calculateScore());
        System.out.printf("[%15s%15s\n", "==", "]");
        System.out.println(floor2.getMachines().size() + " machines total, " + added + " machines added");


//        System.out.println(machines.size());
//        System.out.println(machines);

    }

}
