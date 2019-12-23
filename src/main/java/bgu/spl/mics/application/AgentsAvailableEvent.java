package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import org.javatuples.Pair;

import java.util.List;


public class AgentsAvailableEvent<T> implements Event<T> {

    private List<String> serialAgentsNumbers;
    private Future<T> fut;

    public AgentsAvailableEvent(List<String> serialAgentsNumbers) {
        this.serialAgentsNumbers=serialAgentsNumbers;
        this.fut = new Future<>();
    }

    public List<String> getSerialAgentsNumbers() {
        return serialAgentsNumbers;
    }

    public Future<T> getFut(){
        return fut;
    }

    public void resolveFut(T result){ // Moneypenny will acquire the agents, send her own ID.
        fut.resolve(result);
    }

}
