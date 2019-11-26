package FactoryFloor;

import FactoryFloor.Floor;
import Machine.Transformations;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FloorRunnable implements Runnable {

    ArrayList<Floor> floors;
    ThreadLocalRandom tlr;
    CountDownLatch latch;
    boolean debug;

    public FloorRunnable (ArrayList<Floor> floors, CountDownLatch latch, boolean debug){
        this.latch = latch;
        this.floors = floors;
        tlr = ThreadLocalRandom.current();
        this.debug = debug;
    }

    @Override
    public void run(){
        List<Floor> workingSet = floors.stream().filter(x -> !x.isSwapped())
                .collect(Collectors.toList());
        if (workingSet.size()<2){
//            latch.countDown();
            return;
        }
        Floor[] twoFloors = new Floor[]{workingSet.remove(tlr.nextInt(workingSet.size())),
                workingSet.remove(tlr.nextInt(workingSet.size()))};
        Arrays.sort(twoFloors, Comparator.naturalOrder());
        try {
            while (!twoFloors[0].lock.tryLock(tlr.nextInt(500), TimeUnit.MILLISECONDS)){
                floors.add(twoFloors[0]);
                twoFloors[0] = floors.remove(tlr.nextInt(floors.size()));
            }
            try {
                while (!twoFloors[1].lock.tryLock(tlr.nextInt(500), TimeUnit.MILLISECONDS)){
                    floors.add(twoFloors[1]);
                    twoFloors[1] = floors.remove(tlr.nextInt(floors.size()));
                }
                twoFloors[0].swap(twoFloors[1]);
                latch.countDown();
            }finally {
                if (twoFloors[1].lock.isHeldByCurrentThread()) {
                    twoFloors[1].lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (twoFloors[0].lock.isHeldByCurrentThread()) {
                twoFloors[0].lock.unlock();
            }
        }
    }

}