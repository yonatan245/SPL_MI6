package bgu.spl.mics.application;

import bgu.spl.mics.Broadcast;

import java.util.concurrent.TimeUnit;

public class TickBroadcast implements Broadcast {

    private long CurrentTime;

    public TickBroadcast(long CurrentTime){
        this.CurrentTime=CurrentTime;
    }

    public long getCurrentTime() {
        return CurrentTime;
    }
}
