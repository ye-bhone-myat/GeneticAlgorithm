import FactoryFloor.Floor;
import FactoryFloor.FloorRunnable;
import FactoryFloor.IDGenerator;
import Machine.Machines.AbstractMachine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FloorsCallable implements Callable {

    private boolean debug;
    private final ArrayList<Floor> floors;
    private int nThreads;
    private int floorLength, floorWidth, minScore, threshold;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition accessFinished = lock.newCondition();
    private final Condition updateFinished = lock.newCondition();
    private AtomicBoolean accessing = new AtomicBoolean(false);
    private AtomicBoolean updating = new AtomicBoolean(false);
    private boolean updated = false;
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
//            try {
//                lock.lock();
//
//                while (accessing.getAcquire()) {
//                    accessFinished.await();
//                }
//                updating.set(true);

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
            if (!updated) {
                result = new Result(f.getMachines(), f.getScore(), generation);
                updated = true;
            }
//                updating.set(false);
//                updated = true;
//                updateFinished.signal();
            if (f.getScore() > minScore && Math.abs(f.getScore() - last) < threshold) {
//                lock.unlock();
                break;
            }
//            } finally {
//                if (lock.isHeldByCurrentThread()) {
//                    lock.unlock();
//                }
//            }

        }
        long duration = System.currentTimeMillis() - startTime;
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        result.setDuration(duration);
        return result;
    }

    public Result getUpdatedResult() {
        while (!updated) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.updated = false;
        return result;
    }

}
