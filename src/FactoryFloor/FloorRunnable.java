package FactoryFloor;

import FactoryFloor.Floor;
import Machine.Transformations;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FloorRunnable implements Callable {

    private List<Floor> solutions;
    ThreadLocalRandom tlr;
    CountDownLatch latch;
    boolean debug;

    public FloorRunnable (List<Floor> floors, CountDownLatch latch, boolean debug){
        this.latch = latch;
        solutions = floors;
        tlr = ThreadLocalRandom.current();
        this.debug = debug;
    }

    Floor getRandom(List<Floor> pool){
        if (pool.size() > 1) {
            return pool.get(tlr.nextInt(pool.size()));
        } else {
            return pool.get(0);
        }
    }

    @Override
    public void run() {
        String thread = Thread.currentThread().getId() + "";
        List<Floor> workingSet = solutions.stream().filter(x -> !x.isSwapped())
                .collect(Collectors.toCollection(ArrayList::new));
        if (workingSet.size() == 0){
            latch.countDown();
            if (debug){
                System.out.println("Thread [" + thread + "] nothing left to process, latch is at " + latch.getCount());
            }
            return ;
        }
        Floor f1 = getRandom(workingSet);

        try {
            if (!f1.lock.tryLock(1, TimeUnit.SECONDS) ){
                if (debug) {
                System.out.println("Thread [" + thread + "] awaiting f1 lock...");
                }
//                f1 = getRandom(workingSet);
                return;
            }
            workingSet.remove(f1);
            if (workingSet.size() == 0){
                latch.countDown();
                if (debug){
                    System.out.println("Thread [" + thread + "] nothing left to process, latch is at " + latch.getCount());
                }
                return ;
            }
            Floor f2 = getRandom(workingSet);
            try {
                if ((!f2.lock.tryLock(1, TimeUnit.SECONDS))){
                    if (debug) {
                    System.out.println("Thread [" + thread + "] awaiting f2 lock...");
                    }
//                    f2 = getRandom(workingSet);
                    return;
                }
                f1.swap(f2);
                latch.countDown();
                if (debug) {
                System.out.println("Thread [" + thread + "] completed, latch is at " + latch.getCount());
                }
            } catch (InterruptedException e){
                System.out.println("Thread [" + thread + "] interrupted");
            }finally {
                if (f2.lock.isHeldByCurrentThread()) {
                    f2.lock.unlock();
                }
            }

        } catch (InterruptedException e){
            System.out.println("Thread [" + thread + "] interrupted");
        }
        finally {
            if (f1.lock.isHeldByCurrentThread()) {
                f1.lock.unlock();
            }
        }
//        Floor f2 = solutions.get(Transformations.r.nextInt(10));
//        while ( || !f1.swap(f2)) {
//            f1 = solutions.get(Transformations.r.nextInt(10));
//            f2 = solutions.get(Transformations.r.nextInt(10));
//        }
//        latch.countDown();
    }

    @Override
    public Object call() throws Exception {
        return null;
    }
}