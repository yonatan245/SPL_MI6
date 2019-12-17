package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.MissionReceivedEvent;
import bgu.spl.mics.application.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
    private Map<Long, MissionInfo> missions;
    private long CurrentTime;
    private Future<MissionInfo> fut;

    public Intelligence(String name, TreeMap<Long, MissionInfo> missions) {
        super(name);
        this.missions=missions;
        CurrentTime=0;
        fut=null;
    }


    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, c -> {
            CurrentTime = c.getCurrentTime();
            if(missions.containsKey(CurrentTime)){
                Event<MissionInfo> toSend = new MissionReceivedEvent(missions.get(CurrentTime));
                fut=this.getSimplePublisher().sendEvent(toSend);
            }
        });
    }
}