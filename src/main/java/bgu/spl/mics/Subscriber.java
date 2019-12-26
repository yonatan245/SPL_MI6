package bgu.spl.mics;

import bgu.spl.mics.application.*;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Report;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Subscriber is an abstract class that any subscriber in the system
 * must extend. The abstract Subscriber class is responsible to get and
 * manipulate the singleton {@link MessageBroker} instance.
 * <p>
 * Derived classes of Subscriber should never directly touch the MessageBroker.
 * the derived class also supplies a {@link Callback} that should be called when
 * a message of the subscribed type was taken from the Subscriber
 * message-queue (see {@link MessageBroker#register(Subscriber)}
 * method). The abstract Subscriber stores this callback together with the
 * type of the message is related to.
 * 
 * Only private fields and methods may be added to this class.
 * <p>
 */
public abstract class Subscriber extends RunnableSubPub {

    //Fields
    private boolean terminated = false;
    private Map<Class,Callback> map;

    /**
     * @param name the Subscriber name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public Subscriber(String name) {
        super(name);
        map = new ConcurrentHashMap<>();
    }

    /**
     * Subscribes to events of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to events in the singleton MessageBroker using the supplied
     * {@code type}
     * 2. Store the {@code callback} so that when events of type {@code type}
     * are received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <E>      The type of event to subscribe to.
     * @param <T>      The type of result expected for the subscribed event.
     * @param type     The {@link Class} representing the type of event to
     *                 subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this Subscriber message
     *                 queue.
     */
    protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
    map.put(type, callback);
    MessageBrokerImpl.getInstance().subscribeEvent(type,this);
    }

    /**
     * Subscribes to broadcast message of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to broadcast messages in the singleton MessageBroker using the
     * supplied {@code type}
     * 2. Store the {@code callback} so that when broadcast messages of type
     * {@code type} received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <B>      The type of broadcast message to subscribe to
     * @param type     The {@link Class} representing the type of broadcast
     *                 message to subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this Subscriber message
     *                 queue.
     */
    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
        map.put(type, callback);
        MessageBrokerImpl.getInstance().subscribeBroadcast(type,this);
    }

    /**
     * Completes the received request {@code e} with the result {@code result}
     * using the MessageBroker.
     * <p>
     * @param <T>    The type of the expected result of the processed event
     *               {@code e}.
     * @param e      The event to complete.
     * @param result The result to resolve the relevant Future object.
     *               {@code e}.
     */
    protected final <T> void complete(Event<T> e, T result) {
        MessageBrokerImpl.getInstance().complete(e, result);
    }

    /**
     * Signals the event loop that it must terminate after handling the current
     * message.
     */
    protected final void terminate() {
        terminated = true;
        Thread.currentThread().interrupt();
    }

    /**
     * The entry point of the Subscriber. TODO: you must complete this code
     * otherwise you will end up in an infinite loop.
     */
    @Override
    public final void run() {
        try {
            initialize();
        } catch (InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        while (!terminated) {
            try {
                Message received = MessageBrokerImpl.getInstance().awaitMessage(this);
                executeMessage(received);

            } catch (NullPointerException e) {
                terminate();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                terminate();
            }
        }

        MessageBrokerImpl.getInstance().unregister(this);
    }

    private void executeMessage(Message received) throws ClassNotFoundException, InterruptedException, NullPointerException {
        String whichType = received.getClass().getName();


        switch (whichType) {
            case Names.MISSION_RECEIVED_EVENT:
                Callback<MissionReceivedEvent> missionReceivedEventCallback = map.get(MissionReceivedEvent.class);
                missionReceivedEventCallback.call((MissionReceivedEvent) received);
                break;

            case Names.AGENTS_AVAILABLE_EVENT:
                Callback<AgentsAvailableEvent> agentsAvailableEventCallback = map.get(AgentsAvailableEvent.class);
                agentsAvailableEventCallback.call((AgentsAvailableEvent) received);
                break;

            case Names.GADGET_AVAILABLE_EVENT:
                Callback<GadgetAvailableEvent> gadgetAvailableEventCallback = map.get(GadgetAvailableEvent.class);
                gadgetAvailableEventCallback.call((GadgetAvailableEvent) received);
                break;

            case Names.SEND_THEM_AGENTS:
                Callback<SendThemAgentsEvent> sendThemAgentsEventCallback = map.get(SendThemAgentsEvent.class);
                sendThemAgentsEventCallback.call((SendThemAgentsEvent) received);
                break;

            case Names.RELEASE_AGENTS_EVENT:
                Callback<ReleaseAgentsEvent> releaseAgentsEventCallback = map.get(ReleaseAgentsEvent.class);
                releaseAgentsEventCallback.call((ReleaseAgentsEvent) received);
                break;

            case Names.TICK_BROADCAST:
                Callback<TickBroadcast> tickBroadcastCallback = map.get(TickBroadcast.class);
                tickBroadcastCallback.call((TickBroadcast) received);
                break;

            case Names.TERMINATE_ALL_BROADCAST:
                Callback<TerminateAllBroadcast> terminateAllBroadcastCallback = map.get(TerminateAllBroadcast.class);
                terminateAllBroadcastCallback.call((TerminateAllBroadcast) received);
            }
        }
    }

