package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

import java.util.List;

public class ReleaseAgentsEvent<T> implements Event<T> {

    private List<String> serialAgentsNumbers;
    private Future<Boolean> fut;

    public ReleaseAgentsEvent(List<String> serialAgentsNumbers){
        this.serialAgentsNumbers=serialAgentsNumbers;
    }


    public List<String> getSerialAgentsNumbers(){
        return serialAgentsNumbers;
    }

    public void resolveFut(boolean result){ //Moneypenny will release the agents and return true.
        fut.resolve(result);
    }

    public Future<Boolean> getFut(){return fut;}
}
