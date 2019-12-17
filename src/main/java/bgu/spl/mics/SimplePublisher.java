package bgu.spl.mics;

import bgu.spl.mics.application.AgentsAvailableEvent;
import bgu.spl.mics.application.GadgetAvailableEvent;
import bgu.spl.mics.application.MissionReceivedEvent;
import bgu.spl.mics.application.SendThemAgentsEvent;

/**
 * The SimplePublisher is a class that any publisher in the system
 * stores. The SimplePublisher class is responsible to send
 * messages to the singleton {@link MessageBroker} instance.
 * <p>
 *
 * Only private fields and methods may be added to this class.
 * <p>
 */
public final class SimplePublisher {

    private MessageBroker mb;

    public SimplePublisher(){
        mb=MessageBrokerImpl.getInstance();
    }



    /**
     * Sends the event {@code e} using the MessageBroker and receive a {@link Future<T>}
     * object that may be resolved to hold a result. This method must be Non-Blocking since
     * there may be events which do not require any response and resolving.
     * <p>
     * @param <T>       The type of the expected result of the request
     *                  {@code e}
     * @param e         The event to send
     * @return  		{@link Future<T>} object that may be resolved later by a different
     *         			subscriber processing this event.
     * 	       			null in case no Subscriber has subscribed to {@code e.getClass()}.
     */
    public final <T> Future<T> sendEvent(Event<T> e) {
    String whichEvent = e.getClass().toString();
    Future<T> fut;
    switch(whichEvent){
        case "MissionReceivedEvent" :
            fut = mb.sendEvent((MissionReceivedEvent) e);
            return fut;
        case "AgentsAvailableEvent":
            fut = mb.sendEvent((AgentsAvailableEvent) e);
            return fut;
        case "GadgetAvailableEvent":
            fut = mb.sendEvent((GadgetAvailableEvent) e);
            return fut;
        case "SendThemAgentsEvent":
            fut = mb.sendEvent((SendThemAgentsEvent) e);
            return fut;
    }
    return null;
    }

    /**
     * A Publisher calls this method in order to send the broadcast message {@code b} using the MessageBroker
     * to all the subscribers subscribed to it.
     * <p>
     * @param b The broadcast message to send
     */
    public final void sendBroadcast(Broadcast b) {
        mb.sendBroadcast(b);
    }
}
