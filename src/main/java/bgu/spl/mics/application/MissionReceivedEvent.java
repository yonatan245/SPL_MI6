package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;
import java.util.List;

public class MissionReceivedEvent<T> implements Event<T> {

    //Fields
    private MissionInfo mission;
    private String missionName;
    private Future<T> fut;

    //Constructor
    public MissionReceivedEvent(MissionInfo m){
        this.mission = m;
        fut=new Future<T>();
        missionName = mission.getMissionName();
    }

    //Methods
    public MissionInfo getMission() {
        return mission;
    }

    public List<String> AgentsRequired(){
        return mission.getSerialAgentsNumbers();
    }

    public String GadgetRequired(){
        return mission.getGadget();
    }

    public int getTimeIssued(){
        return mission.getTimeIssued();
    }

    public Future<T> getFut() {
        return fut;
    }

    public void resolveFuture(T result){
        fut.resolve(result);
    }
}
