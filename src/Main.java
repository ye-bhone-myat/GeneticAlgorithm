import FactoryFloor.Floor;
import FactoryFloor.FloorRunnable;
import FactoryFloor.IDGenerator;
import Machine.Shapes;

import java.util.*;
import java.util.concurrent.*;

public class Main {


    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("Please give as arguments the number of threads to use, the minimum score" +
                    ", and score threshold");
            System.exit(0);
        }

        int roomWidth = 16;
        int roomHeight = 16;
        final int NSOLUTIONS;
        int nThreads = Integer.parseInt(args[0]);
        int minScore = Integer.parseInt(args[1]);
        int threshold = Integer.parseInt(args[2]);
        boolean verbose = args.length > 3;
        int processorCount = Runtime.getRuntime().availableProcessors();
        Scanner sc = null;
        boolean cont = false;
        if (verbose) {
            System.out.println("RUNNING IN VERBOSE MODE");
            NSOLUTIONS = nThreads;
            sc = new Scanner(System.in);
        } else {
            nThreads = processorCount;
            NSOLUTIONS = nThreads;
        }
        if (processorCount < 32) {
            System.out.println("System has less than 32 cores...");
        }

        System.out.println("Using " + nThreads + " threads and " + NSOLUTIONS + " solutions");


        ArrayList<Floor> floors = new ArrayList<>();
        for (int j = 0; j < NSOLUTIONS; ++j) {
            floors.add(new Floor(roomWidth, roomHeight));
        }
        int generation = 0;
        floors.sort(Comparator.naturalOrder());
        System.out.println("===== Generation [" + generation + "] =====");
        floors.get(0).display();
        System.out.println("Score: " + floors.get(0).getScore());

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        String threadString = "[" + Thread.currentThread().getId() + "]";
        int last = 0;
        long startTime = System.currentTimeMillis();
        while (true) {
            if (verbose) {
                System.out.println("===== Generation [" + generation + "] =====");
                floors.get(0).display();
                System.out.println("Score: " + floors.get(0).getScore());
                System.out.println("Scores: ");
                System.out.print("[");
                floors.forEach(f -> System.out.print(f.getScore() + ", "));
                System.out.println("]");
            } else {
                if (generation % 50 == 0) {
                    System.out.print("===== Generation [" + generation + "] =====  ");
//                    floors.get(0).display();
                    System.out.println("Score: " + floors.get(0).getScore());
                    System.out.print("Scores: ");
                    System.out.print("[");
                    floors.forEach(f -> System.out.print(f.getScore() + ", "));
                    System.out.println("]");
                }
            }
            CountDownLatch latch = new CountDownLatch(NSOLUTIONS);
            ArrayList<Floor> workingList = new ArrayList<>(floors);
            floors.forEach(x -> {
//                workingList.remove(x);
                Floor f = floors.get(ThreadLocalRandom.current().nextInt(workingList.size()));
                executor.execute(new FloorRunnable(x, f, latch, verbose));
            });
            if (verbose) System.out.println("Thread " + threadString + " awaiting latch...");
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // kill the weak
            floors.removeIf(x -> {
                int scoreRaw = x.getScore();
                double score = (scoreRaw == 0) ? 0 : (double) 1 / scoreRaw;
                boolean isKilled = ThreadLocalRandom.current().nextDouble() < score || scoreRaw < 0;
                if (isKilled) {
                    IDGenerator.addKilled(x.getID());
                }
                return isKilled;
            });
            while (floors.size() < NSOLUTIONS) {
                floors.add(new Floor(roomWidth, roomHeight));
            }
            floors.sort(Comparator.naturalOrder());
            ++generation;
            if (verbose && !cont) {
                System.out.println(">>> Press Enter to advance, enter \"continue\" to continue without pause...");
                String c = sc.nextLine();
                System.out.println(c);
                cont = "continue".equalsIgnoreCase(c);

            }
            if (floors.get(0).getScore() > minScore && Math.abs(floors.get(0).getScore() - last) < threshold){
                break;
            }
            last = floors.get(0).getScore();
        }

        executor.shutdown();
        while (!executor.isTerminated()) {

        }

        long endTime = System.currentTimeMillis() - startTime;
        double durationSeconds = ((double) endTime) / 1000;
        System.out.println("===== Generation [" + generation + "] =====");
        floors.get(0).display();
        System.out.println("Score: " + floors.get(0).getScore());
        System.out.println("Scores: ");
        System.out.print("[");
        floors.forEach(f -> System.out.print(f.getScore() + ", "));
        System.out.println("]");
        System.out.println("Time elapsed: " + durationSeconds + " seconds");


//        Floor floor1 = new Floor(16, 16);
//        Floor floor2 = new Floor(16, 16);
//        floor1.display();
//        System.out.println("\n================\n");
//        floor2.display();
//        System.out.println("\n================\n");
//        Thread t = new Thread(new FloorRunnable(floor1, floor2, false));
//        t.start();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        floor1.display();
//        System.out.println("\n================\n");
//        floor2.display();
//        System.out.println("\n================\n");


//        ArrayList<Floor> floors = new ArrayList<>();
//        for (int j = 0; j < NSOLUTIONS; ++j) {
//            floors.add(new Floor(roomWidth, roomHeight));
//        }


//        int generation = 0;
//        floors.sort(Comparator.naturalOrder());
//        System.out.println("===== Generation [" + generation + "] =====");
//        floors.get(0).display();
//        System.out.println("Score: " + floors.get(0).getScore());
//
//        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
//        CountDownLatch latch;
//        int lastMax = 0;
//        String thread = Thread.currentThread().getName();
//        while(floors.get(0).getScore() < masScore) {
//            lastMax = floors.get(0).getScore();
//            latch = new CountDownLatch(NSOLUTIONS/2);
//            ArrayList<Floor> workingList = new ArrayList<>(floors);
//////            System.out.println("Submitting tasks");
////            floors.forEach((x, y) -> {
////                executor.execute(new FloorRunnableA(x, y, latch, debug));
////            });
//            for (int i = 0; i < NSOLUTIONS/2; ++i) {
//
//                executor.execute(new FloorRunnableA(floors, latch, debug));
//            }
////            System.out.println("Thread [" + thread + "] awaiting latch...");
//            try {
//                latch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            floors.forEach(Floor::calculateScore);
//            floors.sort(Comparator.naturalOrder());
//            floors.removeIf(x -> {
//                int scoreRaw = x.getScore();
//                double score = (scoreRaw == 0) ? 0 : (double) 1/scoreRaw;
//                return r.nextDouble() > score;
//            });
//            while (floors.size() < NSOLUTIONS) {
//                Floor f = new Floor(roomWidth, roomHeight);
//                for (int i = 0; i < max; ++i) {
//                    int tries = 0;
//                    Shapes shape = shapes.get(r.nextInt(shapes.size()));
//                    boolean placed = f.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
//                    while (!placed && tries < 10) {
//                        placed = f.place(shape, r.nextInt(roomHeight), r.nextInt(roomWidth));
//                        ++tries;
//                    }
//                    if (!placed) {
//                        --i;
//                    }
//                }
//                floors.add(f);
//            }
//            floors.forEach(x ->{
//                x.calculateScore();
//                x.resetSwapped();
//            });
//            floors.sort(Comparator.naturalOrder());
//            generation++;
//            if (debug) {
//                System.out.print("===== Generation [" + generation + "] ===== ");
////            floors.get(0).display();
//                System.out.print("Score: " + floors.get(0).getScore() + "\r");
//            } else {
//                if (generation % 20 == 0){
//                    System.out.print("===== Generation [" + generation + "] ===== ");
////            floors.get(0).display();
//                    System.out.println("Score: " + floors.get(0).getScore() + "\r");
//                }
//            }
//        }
//
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//
//        }
//
//        System.out.println("===== Generation [" + generation + "] =====");
//        floors.get(0).display();
//        System.out.println("Score: " + floors.get(0).getScore());
//        System.out.println("Scores: ");
//        System.out.print("[");
//        floors.forEach(f -> System.out.print(f.getScore() + ", "));
//        System.out.println("]");
//        long endTime = System.currentTimeMillis() - startTime;
//        double durationSeconds = ((double) endTime) / 1000;
//        System.out.println("Time elapsed: " + durationSeconds + " seconds");
//        System.out.println(NSOLUTIONS/nThreads + " Solutions per thread");
//        System.out.println("Average " + endTime/NSOLUTIONS + " milliseconds per solution");

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
