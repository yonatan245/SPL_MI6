package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class AgentsAvailableEvent<T> implements Event<T> {

    //Fields
    private List<String> serialAgentsNumbers;
    private Future<T> fut;
    private AtomicInteger time;

    //Constructor
    public AgentsAvailableEvent(List<String> serialAgentsNumbers, int time) {
        this.serialAgentsNumbers = serialAgentsNumbers;
        this.fut = new Future<>();
        this.time = new AtomicInteger(time);
    }

    //Methods
    public List<String> getSerialAgentsNumbers() {
        return serialAgentsNumbers;
    }

    public int getTime(){ return time.get();}

    public Future<T> getFut(){
        return fut;
    }

    public void resolveFut(T result){ // Moneypenny will acquire the agents, send her own ID.
        fut.resolve(result);
    }

}
