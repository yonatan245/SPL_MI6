package bgu.spl.mics;

import bgu.spl.mics.application.*;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Report;
import org.javatuples.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

	//Fields
	private AtomicInteger currentTime;

	private Map<String, EventTopic> eventTopics;
	private Map<String, List<Subscriber>> broadcastTopics;
	private ConcurrentMap<Subscriber, BlockingQueue<Message>> personalQueues;

	//Lock
	private Object messageLock;

	//Constructor
	public static class MessageBrokerImplHolder{
		static private MessageBroker instance = new MessageBrokerImpl();

		public static MessageBroker getInstance(){ return instance;}
	}
	private MessageBrokerImpl(){

		currentTime = new AtomicInteger(0);

		eventTopics = new ConcurrentHashMap<>();
		eventTopics.put(Names.MISSION_RECEIVED_EVENT, new EventTopic());
		eventTopics.put(Names.AGENTS_AVAILABLE_EVENT, new EventTopic());
		eventTopics.put(Names.GADGET_AVAILABLE_EVENT, new EventTopic());
		eventTopics.put(Names.RELEASE_AGENTS_EVENT, new EventTopic());
		eventTopics.put(Names.SEND_THEM_AGENTS, new EventTopic());

		broadcastTopics = new ConcurrentHashMap<>();
		broadcastTopics.put(Names.TICK_BROADCAST, new CopyOnWriteArrayList<>());
		broadcastTopics.put(Names.TERMINATE_ALL_BROADCAST, new CopyOnWriteArrayList<>());

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
		EventTopic toSync = eventTopics.get(type.getName());
		synchronized (toSync) {
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
		if(b.getClass().getName().equals(Names.TICK_BROADCAST)) currentTime.incrementAndGet();

		for (Subscriber subscriber : broadcastTopics.get(b.getClass().getName()))	personalQueues.get(subscriber).add(b);
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e){
		String type = e.getClass().getName();
		Future<T> future;

		Subscriber receiver = eventTopics.get(type).getNextSubscriber();

		personalQueues.get(receiver).add(e);
		future = getEventFuture(e);

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
		if(Thread.currentThread().isInterrupted()) throw new InterruptedException();
		return personalQueues.get(m).take();
	}

	private <T> Future<T> getEventFuture(Event<T> event){
		String type = event.getClass().getName();
		Future<T> output;

		switch(type){
			case Names.AGENTS_AVAILABLE_EVENT:
				AgentsAvailableEvent agentsAvailableEvent = (AgentsAvailableEvent)event;
				output = agentsAvailableEvent.getFut();
				break;

			case Names.GADGET_AVAILABLE_EVENT:
				GadgetAvailableEvent gadgetAvailableEvent = (GadgetAvailableEvent)event;
				output = gadgetAvailableEvent.getFut();
				break;

			case Names.MISSION_RECEIVED_EVENT:
				MissionReceivedEvent missionReceivedEvent = (MissionReceivedEvent)event;
				output = missionReceivedEvent.getFut();
				break;

			case Names.RELEASE_AGENTS_EVENT:
				ReleaseAgentsEvent releaseAgentsEvent = (ReleaseAgentsEvent)event;
				output = releaseAgentsEvent.getFut();
				break;

			case Names.SEND_THEM_AGENTS:
				SendThemAgentsEvent sendThemAgentsEvent = (SendThemAgentsEvent)event;
				output = sendThemAgentsEvent.getFut();
				break;

			default:
				output = null;
		}

		return output;
	}




}
