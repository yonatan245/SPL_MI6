package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.MissionReceivedEvent;
import bgu.spl.mics.application.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;


/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
    private List<MissionInfo> missions;
    private long CurrentTime;
    private Future<MissionInfo> fut;

    public Intelligence(String name, ArrayList<MissionInfo> missions) {
        super(name);
        this.missions=missions;
        CurrentTime=0;
        missions.sort(Comparator.comparing(MissionInfo::getTimeIssued));
        fut=null;
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, c -> CurrentTime=c.getCurrentTime());
        ListIterator<MissionInfo> iter = missions.listIterator();
        while(iter.hasNext()){
              if(CurrentTime==((MissionInfo) iter).getTimeIssued())  {
                  Event<MissionInfo> toSend = new MissionReceivedEvent((MissionInfo)iter);
                  fut=this.getSimplePublisher().sendEvent(toSend);
                  fut.resolve((MissionInfo) iter);
                  iter.next();
              }
              else
              {
                  try {
                      wait();
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
              }


    }

}