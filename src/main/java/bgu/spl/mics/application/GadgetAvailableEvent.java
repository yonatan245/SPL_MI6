package bgu.spl.mics.application;

import bgu.spl.mics.Event;

import java.util.List;

public class GadgetAvailableEvent<T> implements Event<T> {
    private String gadget;

    public GadgetAvailableEvent(String gadget){
        this.gadget=gadget;
    }

    public String getGadget(){
        return gadget;
    }
}
