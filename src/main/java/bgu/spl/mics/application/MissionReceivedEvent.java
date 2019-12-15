package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;

public class MissionReceivedEvent implements Event {

    private MissionInfo m;
    enum Status
    {
        COMPLETED,ABORTED,IN_PROGRESS, PENDING;
    }
    private Future<Report> fut;
    private Status status;

    public MissionReceivedEvent(MissionInfo m){
        this.m=m;
        fut = new Future<Report>();
        status=Status.PENDING;
    }

    public Future<Report> getFuture(){
        return fut;
    }

    public void setAgents(Agent[] toSet){
        for(Agent a:toSet){

        }
        m.setSerialAgentsNumbers();
    }



}
