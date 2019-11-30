package edu.oswego.csc375.FactoryFloor;

import java.util.ArrayDeque;

public final class IDGenerator {
    private static int count = 0;
    private static ArrayDeque<Integer> killedIDs = new ArrayDeque<>();

    public static synchronized int nextID(){
        int id;
        if (killedIDs.isEmpty()){
            id = count;
            count ++;
        } else {
            id = killedIDs.pop();
            if (!(id < count)){
                throw new IllegalStateException();
            }
        }
        return id;
    }

    public static synchronized void addKilled(int killed){
        killedIDs.add(killed);
    }

}
