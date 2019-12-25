package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;
import java.util.List;

public class MissionReceivedEvent<T> implements Event<T> {

    private MissionInfo mission;
    private String missionName;

    enum Status
    {
        COMPLETED,ABORTED,IN_PROGRESS, PENDING;
    }
    private Status status;
    private Future<T> fut;
    public MissionReceivedEvent(MissionInfo m){
        this.mission = m;
        status=Status.PENDING;
        fut=new Future<T>();
        missionName = mission.getMissionName();
    }

    public MissionInfo getMission() {
        return mission;
    }

    public void setStatus(String status){
        switch(status){
            case "ABORTED": this.status=Status.ABORTED;
            case "COMPLETED" : this.status=Status.COMPLETED;
            case "IN_PROGRESS": this.status=Status.IN_PROGRESS;
        };
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

    public String getMissionName() {return missionName;} //TODO: Delete before submission
}
