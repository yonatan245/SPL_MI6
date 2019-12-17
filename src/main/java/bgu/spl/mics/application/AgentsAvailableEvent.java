package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Agent;
import jdk.internal.net.http.common.Pair;

import java.util.List;


public class AgentsAvailableEvent<T> implements Event<T> {

    private List<String> serialAgentsNumbers;
    private Future<Pair<List<Agent>,Long>> fut;

    public AgentsAvailableEvent(List<String> serialAgentsNumbers) {
        this.serialAgentsNumbers=serialAgentsNumbers;
    }

    public List<String> getSerialAgentsNumbers() {
        return serialAgentsNumbers;
    }

    public Future<Pair<List<Agent>,Long>> getFut(){
        return fut;
    }

    public void resolveFut(Pair<List<Agent>,Long> result){ // Moneypenny will acquire the agents, send a list made of copies of the agents and her own ID.
        fut.resolve(result);
    }

}
