package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SendThemAgentsEvent<T> implements Event<T> {

    //Fields
    private List<String> serialAgentsNumbers;
    private int duration;
    private Future<T> fut;
    private AtomicInteger time;

    //Constructor
    public SendThemAgentsEvent(List<String> serialAgentsNumbers, int duration, int time){
        this.serialAgentsNumbers=serialAgentsNumbers;
        this.duration=duration;
        fut = new Future<>();
        this.time = new AtomicInteger(time);
    }

    //Methods
    public int getTime() {
        return time.get();
    }

    public int getDuration(){
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

