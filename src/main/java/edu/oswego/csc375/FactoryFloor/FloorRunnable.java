package edu.oswego.csc375.FactoryFloor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class FloorRunnable implements Runnable {

    Floor floor1, floor2;
//    ThreadLocalRandom tlr;
    private CountDownLatch latch;
    boolean debug;

    public FloorRunnable(Floor f1, Floor f2, CountDownLatch latch, boolean debug){
        this.latch = latch;
        floor1 = f1;
        floor2 = f2;
//        tlr = ThreadLocalRandom.current();
        this.debug = debug;
    }

    @Override
    public void run(){
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        int mutateTimes = tlr.nextInt(10);
        String threadName = "[" + Thread.currentThread().getId() + "]";
        if (debug){
            System.out.println("Thread " + threadName + " mutating " + mutateTimes + " times...");
        }
        for (int i = 0; i < mutateTimes; ++i){
            floor1.mutate();
        }
        int id1 = floor1.getID();
        int id2 = floor2.getID();
        if (!floor1.equals(floor2) && tlr.nextInt(5) < 1){
            if (floor1.getID() < floor2.getID()){
                if (debug){
                    System.out.println("Thread " + threadName + " swapping f" + id1 + " and f" + id2 + "...");
                }
                floor1.swap(floor2);
            } else {
                if (debug){
                    System.out.println("Thread " + threadName + " f" + id2 + " precedes f" + id1 + "...");
                    System.out.println("Thread " + threadName + " swapping f" + id2 + " and f" + id1 + "...");
                }
                floor2.swap(floor1);
            }
        }
        latch.countDown();
        if (debug){
            System.out.println("Thread " + threadName + " latch down at " + latch.getCount());
        }
    }

}