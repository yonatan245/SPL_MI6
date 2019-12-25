package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class AgentsAvailableEvent<T> implements Event<T> {

    private List<String> serialAgentsNumbers;
    private Future<T> fut;
    private AtomicInteger time;
    private String missionName;


    public AgentsAvailableEvent(List<String> serialAgentsNumbers, int time, String missionName) {
        this.serialAgentsNumbers = serialAgentsNumbers;
        this.fut = new Future<>();
        this.time = new AtomicInteger(time);
        this.missionName = missionName;
    }

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

    public String getMissionName() {return missionName;} //TODO: Delete before submission
}
