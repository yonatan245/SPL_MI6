package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Agent;
import jdk.internal.net.http.common.Pair;

import java.util.List;


public class AgentsAvailableEvent<T> implements Event<T> {

    private List<String> serialAgentsNumbers;
    private Future<Pair<List<String>,Long>> fut;

    public AgentsAvailableEvent(List<String> serialAgentsNumbers) {
        this.serialAgentsNumbers=serialAgentsNumbers;
    }

    public List<String> getSerialAgentsNumbers() {
        return serialAgentsNumbers;
    }

    public Future<Pair<List<String>,Long>> getFut(){
        return fut;
    }

    public void resolveFut(Pair<List<String>,Long> result){ // Moneypenny will acquire the agents, send her own ID.
        fut.resolve(result);
    }

}
