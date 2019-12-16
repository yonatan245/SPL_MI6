package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.MissionReceivedEvent;
import bgu.spl.mics.application.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
    private Future<Report> fut;
    private MissionInfo[] missions;
    private long CurrentTime;

    public Intelligence(String name,MissionInfo[] missions) {
        super(name);
        this.missions=missions;
        CurrentTime=0;
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, c -> CurrentTime=c.getCurrentTime());
        for(MissionInfo m:missions) {
            Event<MissionInfo> toPublish = new MissionReceivedEvent(m);

        }
    }

}