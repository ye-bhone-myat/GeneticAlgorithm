package FactoryFloor;

import FactoryFloor.Floor;
import Machine.Transformations;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class FloorRunnable implements Runnable{

    private List<Floor> solutions;
    ThreadLocalRandom tlr;
    CountDownLatch latch;

    public FloorRunnable (List<Floor> floors, CountDownLatch latch){
        this.latch = latch;
        solutions = floors;
        tlr = ThreadLocalRandom.current();
    }

    Floor getRandom(){
        return solutions.get(tlr.nextInt(solutions.size()));
    }

    @Override
    public void run() {
        String thread = Thread.currentThread().getId() + "";
        Floor f1 = getRandom();
//        int count = (int) solutions.stream().filter(x -> x.isSwapped()).count();
//        if (count == solutions.size()){
//            return ;
//        }
        try {
            while (!f1.lock.tryLock() ){
//                System.out.println("Thread [" + thread + "] awaiting f1 lock...");
                f1 = getRandom();
            }
            Floor f2 = getRandom();
            try {
                while (!f2.lock.tryLock() || f1.equals(f2)){
//                    System.out.println("Thread [" + thread + "] awaiting f2 lock...");
                    f2 = getRandom();
                }
                f1.swap(f2);
                latch.countDown();
//                System.out.println("Thread [" + thread + "] completed, latch is at " + latch.getCount());
            } finally {
                f2.lock.unlock();
            }

        } finally {
            f1.lock.unlock();
        }
//        Floor f2 = solutions.get(Transformations.r.nextInt(10));
//        while ( || !f1.swap(f2)) {
//            f1 = solutions.get(Transformations.r.nextInt(10));
//            f2 = solutions.get(Transformations.r.nextInt(10));
//        }
//        latch.countDown();
    }
}