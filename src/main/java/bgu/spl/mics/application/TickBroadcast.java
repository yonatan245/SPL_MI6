package bgu.spl.mics.application;

import bgu.spl.mics.Broadcast;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TickBroadcast implements Broadcast {

    private AtomicLong currentTime;

    public TickBroadcast(long CurrentTime){
        currentTime = new AtomicLong(CurrentTime);
    }

    public long getCurrentTime() {
        return currentTime.get();
    }
}
