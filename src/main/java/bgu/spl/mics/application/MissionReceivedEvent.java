package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;
import bgu.spl.mics.application.subscribers.M;

import java.util.ArrayList;
import java.util.List;

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

    public void setStatus(Status status){
        this.status=status;
    }

    public List<String> AgentsRequired(){
        return m.getSerialAgentsNumbers();
    }

    public String GadgetRequired(){
        return m.getGadget();
    }

    public int getTimeIssued(){
        return m.getTimeIssued();
    }

    public void MissionComplete(int mId, int moneyPennyId, List<String> agentsSerialNumbers, List<String> agentsNames
            , int QTime, int timeCreated){
            Report result = new Report(m.getMissionName(), mId, moneyPennyId, agentsSerialNumbers, agentsNames, m.getGadget(), m.getTimeIssued(), QTime, timeCreated);
            fut.resolve(result);
            status=Status.COMPLETED;
        }
    }
