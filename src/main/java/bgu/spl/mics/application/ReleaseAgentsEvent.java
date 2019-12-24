package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ReleaseAgentsEvent<T> implements Event<T> {

    private List<String> serialAgentsNumbers;
    private Future<T> fut;
    private AtomicInteger time;

    public ReleaseAgentsEvent(List<String> serialAgentsNumbers, int time){
        fut = new Future<>();
        this.serialAgentsNumbers=serialAgentsNumbers;
        this.time = new AtomicInteger(time);
    }

    public int getTime() {
        return time.get();
    }

    public List<String> getSerialAgentsNumbers(){
        return serialAgentsNumbers;
    }

    public void resolveFut(T result){ //Moneypenny will release the agents and return true.
        fut.resolve(result);
    }

    public Future<T> getFut(){return fut;}
}
