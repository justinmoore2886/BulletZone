package edu.unh.cs.cs619.bulletzone.util;

import java.util.concurrent.atomic.AtomicLong;

public class ValueGenerator {
    private static ValueGenerator _instance;

    private static int [] randomCellIndexArr = new int[256];
    private static int curIndex = 0;

    private final AtomicLong playerID = new AtomicLong(1);
    private final AtomicLong restrictedID = new AtomicLong(31);

    private static final Object monitor = new Object();

    private ValueGenerator() {
        // Fills array with indices for grid
        for(int i=0;i<256;i++){
            randomCellIndexArr[i] = i;
        }
        shuffleRandomCellIndexArr();
    }

    // something like the Fisher-Yates shuffling algorithm
    // randomizes array of indices
    private void shuffleRandomCellIndexArr(){
        for(int i=0;i<256;i++){
            int rand = (int)((Math.random() * (256 - i)) + i);
            int base = randomCellIndexArr[i];
            randomCellIndexArr[i] = randomCellIndexArr[rand];
            randomCellIndexArr[rand] = base;
        }
    }

    public int getNextRandomIndex(){
        int retIndex;
        synchronized (monitor){
            if(curIndex >= 256){
                shuffleRandomCellIndexArr();
                curIndex = 0;
            }
            retIndex = curIndex++;
        }
        return retIndex;
    }

    public long getNextPlayerID(){
        long retID;
        synchronized (monitor){
            retID = playerID.getAndIncrement();
        }
        return retID;
    }

    public long getNextRestrictedID(){
        long retID;
        synchronized (monitor){
            retID = restrictedID.getAndIncrement();
            if(retID % 10 == 0)
                retID = restrictedID.getAndIncrement();
            if(retID >=89)
                retID = restrictedID.getAndSet(31);
        }
        return retID;
    }

    public static ValueGenerator getInstance(){
        if(_instance == null){
            synchronized (monitor){
                if(_instance == null)
                {
                    _instance = new ValueGenerator();
                }
            }
        }
        return _instance;
    }
}
