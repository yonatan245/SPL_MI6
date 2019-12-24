package bgu.spl.mics;

import bgu.spl.mics.application.*;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Report;
import java.util.*;

import java.util.List;

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

    //Fields
    private MessageBroker mb;

    //Constructor
    public SimplePublisher(){
        mb = MessageBrokerImpl.getInstance();
    }

    //Methods
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
    public final <T> Future<T> sendEvent(Event<T> e) throws ClassNotFoundException, InterruptedException, NullPointerException {
        String whichEvent = e.getClass().getName();
        Future<T> fut = null;

        switch (whichEvent) {
            case Names.MISSION_RECEIVED_EVENT:
                fut = mb.sendEvent((MissionReceivedEvent) e);
                break;

            case Names.AGENTS_AVAILABLE_EVENT:
                fut = mb.sendEvent((AgentsAvailableEvent) e);
                break;

            case Names.GADGET_AVAILABLE_EVENT:
                fut = mb.sendEvent((GadgetAvailableEvent) e);
                break;

            case Names.SEND_THEM_AGENTS:
                fut = mb.sendEvent((SendThemAgentsEvent) e);
                break;

            case Names.RELEASE_AGENTS_EVENT:
                fut = mb.sendEvent((ReleaseAgentsEvent) e);
                break;
        }

        return fut;
    }

    /**
     * A Publisher calls this method in order to send the broadcast message {@code b} using the MessageBroker
     * to all the subscribers subscribed to it.
     * <p>
     * @param b The broadcast message to send
     */
    public final void sendBroadcast(Broadcast b) {
        try {
            mb.sendBroadcast(b);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
