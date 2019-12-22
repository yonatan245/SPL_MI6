package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.List;

public class SendThemAgentsEvent<T> implements Event<T> {

    private List<String> serialAgentsNumbers;
    private long duration;
    private Future<T> fut;

    public SendThemAgentsEvent(List<String> serialAgentsNumbers, long duration){
        this.serialAgentsNumbers=serialAgentsNumbers;
        this.duration=duration;
    }

    public long getDuration(){
        return duration;
    }

    public List<String> getSerialAgentsNumbers(){
        return serialAgentsNumbers;
    }

    public void resolveFut(T result){ //Moneypenny will put the agents to sleep and return true;
        fut.resolve(result);
    }

    public Future<T> getFut(){return fut;}
}

