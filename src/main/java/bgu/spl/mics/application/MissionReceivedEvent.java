package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;
import java.util.List;

public class MissionReceivedEvent<T> implements Event<T> {

    private MissionInfo mission;

    enum Status
    {
        COMPLETED,ABORTED,IN_PROGRESS, PENDING;
    }
    private Status status;
    private Future<Report> fut;
    public MissionReceivedEvent(MissionInfo m){
        this.mission = m;
        status=Status.PENDING;
        fut=new Future<Report>();
    }

    public void setStatus(Status status){
        this.status=status;
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

    public Future<Report> getFut() {
        return fut;
    }

    public void resolveFuture(Report result){
        fut.resolve(result);
    }

    }
