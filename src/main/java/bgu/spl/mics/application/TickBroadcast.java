package bgu.spl.mics.application;

import bgu.spl.mics.Broadcast;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TickBroadcast implements Broadcast {

    private AtomicInteger currentTime;

    public TickBroadcast(int CurrentTime){
        currentTime = new AtomicInteger(CurrentTime);
    }

    public int getCurrentTime() {
        return currentTime.get();
    }
}
