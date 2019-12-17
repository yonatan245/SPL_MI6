package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Agent;
import jdk.internal.net.http.common.Pair;

import java.util.List;

public class GadgetAvailableEvent<T> implements Event<T> {
    private String gadget;
    private Future<String> fut;

    public GadgetAvailableEvent(String gadget){
        this.gadget=gadget;
    }

    public String getGadget(){
        return gadget;
    }

    public void resolveFut(String result){
        fut.resolve(result);
    }

    public Future<String> getFut(){return fut;}
}
