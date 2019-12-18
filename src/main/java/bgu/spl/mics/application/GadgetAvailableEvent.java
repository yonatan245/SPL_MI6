package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import org.javatuples.Pair;

public class GadgetAvailableEvent<T> implements Event<T> {
    private String gadget;
    private Future<Pair<String,Long>> fut;

    public GadgetAvailableEvent(String gadget){
        this.gadget=gadget;
    }

    public String getGadget(){
        return gadget;
    }

    public void resolveFut(Pair<String,Long> result){
        fut.resolve(result);
    }

    public Future<Pair<String,Long>> getFut(){return fut;}
}
