package bgu.spl.mics;

import bgu.spl.mics.application.*;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Report;
import org.javatuples.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

	//Fields
	private long currentTime;

	private Map<String, EventTopic> eventTopics;
	private Map<String, List<Subscriber>> broadcastTopics;
	private Map<Subscriber, Queue<Message>> personalQueues;

	//Lock
	private Object messageLock;

	//Constructor
	public static class MessageBrokerImplHolder{
		static private MessageBroker instance = new MessageBrokerImpl();

		public static MessageBroker getInstance(){ return instance;}
	}
	private MessageBrokerImpl(){

		eventTopics = new ConcurrentHashMap<>();
		eventTopics.put(MISSION_RECEIVED_EVENT, new EventTopic());
		eventTopics.put(AGENTS_AVAILABLE_EVENT, new EventTopic());
		eventTopics.put(GADGET_AVAILABLE_EVENT, new EventTopic());
		eventTopics.put(RELEASE_AGENTS_EVENT, new EventTopic());
		eventTopics.put(SEND_THEM_AGENTS, new EventTopic());

		broadcastTopics = new ConcurrentHashMap<>();
		broadcastTopics.put(TICK_BROADCAST, new CopyOnWriteArrayList<>());

		personalQueues = new ConcurrentHashMap<>();

		//Lock for all who those wait for new messages
		messageLock = new Object();
	}

	//Methods
	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {
		return MessageBrokerImplHolder.getInstance();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		synchronized (eventTopics.get(type.getName())) {
			eventTopics.get(type.getName()).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		synchronized (broadcastTopics.get(type.getName())) {
			broadcastTopics.get(type.getName()).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		String whichEvent = e.getClass().getName();
		switch (whichEvent) {
			case "bgu.spl.mics.MissionReceivedEvent":
				((MissionReceivedEvent) e).resolveFuture((Report) result);
				break;

			case "bgu.spl.mics.AgentsAvailableEvent":
				((AgentsAvailableEvent) e).resolveFut((Pair<List<Agent>, Long>) result);
				break;

			case "bgu.spl.mics.GadgetAvailableEvent":
				((GadgetAvailableEvent) e).resolveFut((Pair<String,Long>) result);
				break;

			case "bgu.spl.mics.SendThemAgentsEvent":
				((SendThemAgentsEvent) e).resolveFut((boolean) result);
				break;

			case "bgu.spl.mics.ReleaseAgentsEvent":
				((ReleaseAgentsEvent) e).resolveFut((boolean) result);
				break;

		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (Subscriber subscriber : broadcastTopics.get(b.getClass().getName()))
			personalQueues.get(subscriber).add(b);
		messageLock.notifyAll();
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e){
		String type = e.getClass().getName();
		Future<T> future;

		synchronized (eventTopics.get(type)) {
			Subscriber receiver = eventTopics.get(type).getNextSubscriber();
			personalQueues.get(receiver).add(e);
			future = getEventFuture(e);
		}

		eventTopics.get(type).notifyAll();
		messageLock.notifyAll();

		return future;
	}

	@Override
	public void register(Subscriber m) {
			personalQueues.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(Subscriber m) {
		personalQueues.remove(m);

		for(EventTopic topic : eventTopics.values()) topic.remove(m);
		for(List<Subscriber> topic : broadcastTopics.values()) topic.remove(m);
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		while(personalQueues.get(m).isEmpty() && !Thread.currentThread().isInterrupted()){
			messageLock.wait();
		}

		if(Thread.currentThread().isInterrupted()) throw new InterruptedException();

		return personalQueues.get(m).element();
	}

	private <T> Future getEventFuture(Event<T> event){
		String type = event.getClass().getName();
		Future output;

		switch(type){
			case AGENTS_AVAILABLE_EVENT:
				AgentsAvailableEvent agentsAvailableEvent = (AgentsAvailableEvent)event;
				output = agentsAvailableEvent.getFut();
				break;

			case GADGET_AVAILABLE_EVENT:
				GadgetAvailableEvent gadgetAvailableEvent = (GadgetAvailableEvent)event;
				output = gadgetAvailableEvent.getFut();
				break;

			case MISSION_RECEIVED_EVENT:
				MissionReceivedEvent missionReceivedEvent = (MissionReceivedEvent)event;
				output = missionReceivedEvent.getFut();
				break;

			case RELEASE_AGENTS_EVENT:
				ReleaseAgentsEvent releaseAgentsEvent = (ReleaseAgentsEvent)event;
				output = releaseAgentsEvent.getFut();
				break;

			case SEND_THEM_AGENTS:
				SendThemAgentsEvent sendThemAgentsEvent = (SendThemAgentsEvent)event;
				output = sendThemAgentsEvent.getFut();
				break;

			default:
				output = null;
		}

		return output;
	}

	private static final String AGENTS_AVAILABLE_EVENT = "bgu.spl.mics.application.AgentsAvailableEvent";
	private static final String GADGET_AVAILABLE_EVENT = "bgu.spl.mics.application.GadgetAvailableEvent";
	private static final String MISSION_RECEIVED_EVENT = "bgu.spl.mics.application.MissionReceivedEvent";
	private static final String RELEASE_AGENTS_EVENT = "bgu.spl.mics.application.ReleaseAgentsEvent";
	private static final String SEND_THEM_AGENTS = "bgu.spl.mics.application.SendThemAgents";
	private static final String TICK_BROADCAST = "bgu.spl.mics.application.TickBroadcast";



}
