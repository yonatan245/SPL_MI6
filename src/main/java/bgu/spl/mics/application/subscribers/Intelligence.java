package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.MissionReceivedEvent;
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

    public void RecieveEvent(){}

    @Override
    protected void initialize() {
        CurrentTime=
        for(MissionInfo m:missions) {
            Event<MissionInfo> toPublish = new MissionReceivedEvent(m);

        }
    }

}