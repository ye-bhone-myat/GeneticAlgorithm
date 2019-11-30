package edu.oswego.csc375;

import edu.oswego.csc375.FactoryFloor.Floor;
import edu.oswego.csc375.FactoryFloor.FloorRunnable;
import edu.oswego.csc375.FactoryFloor.IDGenerator;
import edu.oswego.csc375.FactoryFloor.Tile;
import edu.oswego.csc375.Machine.Machines.AbstractMachine;
import edu.oswego.csc375.Machine.Shapes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class FloorsCallable implements Callable {

    private boolean debug;
    private final ArrayList<Floor> floors;
    private int nThreads;
    private int floorLength, floorWidth, minScore, threshold;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition updateFinished = lock.newCondition();
    private boolean updated;
    private Result result;

    public FloorsCallable(ArrayList<Floor> floors, int length, int width, int nThreads,
                          int minScore, int threshold, boolean debug) {
        this.debug = debug;
        this.floors = floors;
        this.nThreads = nThreads;
        this.floorLength = length;
        this.floorWidth = width;
        this.minScore = minScore;
        this.threshold = threshold;
        Floor f = floors.get(0);
        this.result = null;
        updated = false;
    }

    @Override
    public Result call() throws Exception {


        int generation = 0;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        String threadString = "[" + Thread.currentThread().getId() + "]";
        boolean cont = false;
        int last = 0;
        Scanner sc = null;
        if (debug) {
            sc = new Scanner(System.in);
        }
        long startTime = System.currentTimeMillis();
        while (true) {
            if (debug) {
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
            CountDownLatch latch = new CountDownLatch(nThreads);
            floors.forEach(x -> {
                Floor f = floors.get(ThreadLocalRandom.current().nextInt(floors.size()));
                executor.execute(new FloorRunnable(x, f, latch, debug));
            });
            if (debug) System.out.println("Thread " + threadString + " awaiting latch...");
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
            while (floors.size() < nThreads) {
                floors.add(new Floor(floorWidth, floorLength));
            }
            floors.sort(Comparator.naturalOrder());
            ++generation;
            if (debug && !cont) {
                System.out.println(">>> Press Enter to advance, enter \"continue\" to continue without pause...");
                String c = sc.nextLine();
                System.out.println(c);
                cont = "continue".equalsIgnoreCase(c);

            }
            Floor f = floors.get(0);
            last = f.getScore();
//            if (last == 0 && f.getScore() == 0){
//                f.display();
//                Thread.sleep(0);
//            }

            updateResult(f.getMachines(), last, generation);
            if (f.getScore() > minScore && Math.abs(f.getScore() - last) < threshold) {
                break;
            }

        }
        long duration = System.currentTimeMillis() - startTime;
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        Floor f = floors.get(0);
        updateResult(f.getMachines(), f.getScore(), generation);
        updated = true;
        result.setDuration(duration);
        return result;
    }

    private void updateResult(ArrayList<AbstractMachine> machines, int score, int generation) {
        try {
            lock.lock();
            ArrayList<Tile> occupiedTiles = machines.stream().flatMap(machine -> machine.getGrids().stream())
                    .collect(Collectors.toCollection(ArrayList::new));
            int[] xVals = occupiedTiles.stream().mapToInt(Tile::getX).toArray();
            int[] yVals = occupiedTiles.stream().mapToInt(Tile::getY).toArray();
            Shapes[] shapes = occupiedTiles.stream().map(tile -> tile.getMachine().getShape())
                    .toArray(Shapes[]::new);
            result = new Result(xVals, yVals, shapes, score, generation);
            updated = true;
            updateFinished.signal();
        } finally {
            lock.unlock();
        }
    }

    Result getUpdatedResult() {
        Result r = null;
        try {
            lock.lock();
            while (!updated) {
                updateFinished.await();
            }
            updated = false;
            r = result.clone();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return r;
    }

}
