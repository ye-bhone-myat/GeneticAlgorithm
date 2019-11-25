import FactoryFloor.Floor;
import FactoryFloor.FloorRunnable;
import Machine.Shapes;

import java.util.*;
import java.util.concurrent.*;

public class Main {


    public static void main(String[] args) {


        int roomWidth = 16;
        int roomHeight = 16;
        int NSOLUTIONS = Integer.parseInt(args[0]);
        int nThreads = Integer.parseInt(args[1]);
//        Floor floor1 = new Floor(roomWidth, roomHeight);
//        Floor floor2 = new Floor(roomWidth, roomHeight);
        ArrayList<Shapes> shapes = new ArrayList<>(Arrays.asList(
                Shapes.Square
                , Shapes.ElbowL
                , Shapes.KinkL
                , Shapes.ElbowR
                , Shapes.KinkR
                , Shapes.Rod
        ));
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int max = 32 + r.nextInt(7);
        ArrayList<Floor> floors = new ArrayList<>();
        for (int j = 0; j < NSOLUTIONS; ++j) {
            floors.add(new Floor(roomWidth, roomHeight));
            for (int i = 0; i < max; ++i) {
                int tries = 0;
                Shapes shape = shapes.get(r.nextInt(shapes.size()));
                boolean placed = floors.get(j).place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
                while (!placed && tries < 10) {
                    placed = floors.get(j).place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
                    ++tries;
                }
                if (!placed) {
                    --i;
                }
            }
        }
        int generation = 0;
        floors.sort(Comparator.naturalOrder());
        System.out.println("===== Generation [" + generation + "] =====");
        floors.get(0).display();
        System.out.println("Score: " + floors.get(0).getScore());

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch;
        int lastMax = 0;
        String thread = Thread.currentThread().getName();
        while(floors.get(0).getScore() < 193) {
            lastMax = floors.get(0).getScore();
            latch = new CountDownLatch(NSOLUTIONS);
//            System.out.println("Submitting tasks");
            for (int i = 0; i < NSOLUTIONS; ++i) {
                executor.execute(new FloorRunnable(floors, latch));
            }
//            System.out.println("Thread [" + thread + "] awaiting latch...");
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            floors.sort(Comparator.naturalOrder());
            floors.removeIf(x -> x.getScore() < floors.get(0).getScore()/2);
            while (floors.size() < NSOLUTIONS) {
                Floor f = new Floor(roomWidth, roomHeight);
                for (int i = 0; i < max; ++i) {
                    int tries = 0;
                    Shapes shape = shapes.get(r.nextInt(shapes.size()));
                    boolean placed = f.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
                    while (!placed && tries < 10) {
                        placed = f.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
                        ++tries;
                    }
                    if (!placed) {
                        --i;
                    }
                }
                floors.add(f);
            }
            floors.sort(Comparator.naturalOrder());
            generation++;
            System.out.print("===== Generation [" + generation + "] ===== ");
//            floors.get(0).display();
            System.out.println("Score: " + floors.get(0).getScore());
        }

        executor.shutdown();
        while (!executor.isTerminated()) {

        }

        System.out.println("===== Generation [" + generation + "] =====");
        floors.get(0).display();
        System.out.println("Score: " + floors.get(0).getScore());
        System.out.println("Scores: ");
        floors.forEach(f -> System.out.println(f.getScore()));

//        int max = 4;

////        max = 32 + r.nextInt(7);
//        max = 4;
//        for (int i = 0; i < max; ++i){
//            int tries = 0;
//            Shapes shape = shapes.get(r.nextInt(shapes.size()));
//            boolean placed = floor2.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
//            while (!placed && tries < 10){
//                placed = floor2.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
//                ++ tries;
//            }
//            if (!placed){
//                --i;
//            }
//        }
//        System.out.println("Floor Plan [1]");
//        floor1.display();
//        System.out.println("Score: " + floor1.calculateScore());
//        System.out.printf("[%15s%15s\n", "==", "]");
//        System.out.println("Floor Plan [2]");
//        floor2.display();
//        System.out.println("Score: " + floor2.calculateScore());
//        System.out.printf("[%15s%15s\n", "==", "]");


//        int start = r.nextInt((roomHeight*roomWidth)/2);
//        int end = r.nextInt(start) + start;
//        ArrayList<AbstractMachine> machines1 = floor1.removeMachines(start, end);
//        ArrayList<AbstractMachine> machines2 = floor2.removeMachines(start, end);
//        floor1.clearMachines(start, end);
//        floor2.clearMachines(start, end);
//        floor1.addMachines(machines2);
//        floor2.addMachines(machines1);


//        System.out.println("Floor Plan [1] cut off from " + start + " to " + end + ".");
//        floor1.display();
//        System.out.println("Score: " + floor1.calculateScore());
//        System.out.println("Removed " + machines.size() + " machine(s)");
//        System.out.printf("[%15s%15s\n", "==", "]");
//        System.out.println("Floor Plan [2] cut off  from " + start + " to " + end + ".");
//        floor2.display();
//        System.out.println("Score: " + floor2.calculateScore());
//        System.out.printf("[%15s%15s\n", "==", "]");
//        System.out.println("Floor Plan [2] with added bits from [1]");
//        floor2.display();
//        System.out.println("Score: " + floor2.calculateScore());
//        System.out.printf("[%15s%15s\n", "==", "]");
//        System.out.println(floor2.getMachines().size() + " machines total, " + added + " machines added");
//
////        System.out.println(machines.size());
////        System.out.println(machines);

        // =====


//        ArrayList<TestFloor> testFloors = new ArrayList<>();
//        for (int i = 0; i < 10; ++i) {
//            testFloors.add(new TestFloor());
//        }
//        testFloors.sort(Comparator.naturalOrder());
//        System.out.println(testFloors);
//        int lastMax = 0;
//        while (testFloors.get(0).getValue() < 1000) {
//            lastMax = testFloors.get(0).getValue();
//            latch = new CountDownLatch(testFloors.size());
//            for (int i = 0; i < testFloors.size(); ++i) {
//            }
//            try {
//                latch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            testFloors.sort(Comparator.naturalOrder());
//            int thisMax = testFloors.get(0).getValue();
//            if (Math.abs(lastMax - thisMax) < 4){
//
//            }
//            testFloors.removeIf(x -> {
//
//            });
//            while (testFloors.size()< 10){
//                testFloors.add(new TestFloor());
//            }
//            testFloors.sort(Comparator.naturalOrder());
//        }
//        executor.shutdown();
//
//        while (!executor.isTerminated()) {
//
//        }
//        System.out.println(testFloors);


    }

}
