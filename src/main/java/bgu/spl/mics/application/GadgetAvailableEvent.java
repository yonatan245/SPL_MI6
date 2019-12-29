package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import org.javatuples.Pair;

import java.util.concurrent.atomic.AtomicInteger;

public class GadgetAvailableEvent<T> implements Event<T> {

    //Fields
    private String gadget;
    private Future<T> fut;
    private AtomicInteger time;

    //Constructor
    public GadgetAvailableEvent(String gadget, int time){
        fut = new Future<>();
        this.gadget=gadget;
        this.time = new AtomicInteger(time);

    }

    //Methods
    public String getGadget(){
        return gadget;
    }

    public int getMTime(){
        return time.get();
    }

    public void resolveFut(T result){
        fut.resolve(result);
    }

    public Future<T> getFut(){return fut;}
}
