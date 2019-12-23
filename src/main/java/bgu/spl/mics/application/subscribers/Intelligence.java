package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.MissionReceivedEvent;
import bgu.spl.mics.application.TerminateAllBroadcast;
import bgu.spl.mics.application.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {

    //Fields
    private Map<Integer, List<MissionInfo>> missions;
    private Future<MissionInfo> fut;
    private AtomicLong currentTime;


    //Constructor
    public Intelligence(String name, Map<Integer, List<MissionInfo>> missions) {
        super(name);
        this.missions = missions;
        fut = null;
        currentTime = new AtomicLong(0);
    }

    //Methods
    @Override
    protected void initialize() {
        Thread.currentThread().setName(getName());
        MessageBrokerImpl.getInstance().register(this);
        this.subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
            @Override
            public void call(TerminateAllBroadcast c) throws InterruptedException, ClassNotFoundException {
                terminate();
            }
        });
        this.subscribeBroadcast(TickBroadcast.class, c -> {
            currentTime.set(c.getCurrentTime());
            if(missions.containsKey(currentTime.get())){

                for(MissionInfo mission : missions.get(currentTime.get())){
                    Event newEvent = new MissionReceivedEvent(mission);
                    this.getSimplePublisher().sendEvent(newEvent);
                }
            }
        });


    }
}