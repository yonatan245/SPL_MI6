package bgu.spl.mics;

import bgu.spl.mics.application.*;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Report;
import org.javatuples.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

	//Fields
	private AtomicInteger currentTime;

	private ConcurrentMap<String, BlockingQueue<Subscriber>> eventTopics;
	private ConcurrentMap<String, List<Subscriber>> broadcastTopics;
	private ConcurrentMap<Subscriber, BlockingQueue<Message>> personalEventQueues;
	private ConcurrentMap<Subscriber, BlockingQueue<Message>> personalBroadcastQueues;
	private ConcurrentMap<Subscriber, BlockingQueue<Message>> personalTimeQueues;


	//Constructor
	public static class MessageBrokerImplHolder{
		static private MessageBroker instance = new MessageBrokerImpl();

		public static MessageBroker getInstance(){ return instance;}
	}
	private MessageBrokerImpl(){

		currentTime = new AtomicInteger(0);

		eventTopics = new ConcurrentHashMap<>();
		eventTopics.put(Names.MISSION_RECEIVED_EVENT, new LinkedBlockingQueue<>());
		eventTopics.put(Names.AGENTS_AVAILABLE_EVENT, new LinkedBlockingQueue<>());
		eventTopics.put(Names.GADGET_AVAILABLE_EVENT, new LinkedBlockingQueue<>());
		eventTopics.put(Names.RELEASE_AGENTS_EVENT, new LinkedBlockingQueue<>());
		eventTopics.put(Names.SEND_THEM_AGENTS, new LinkedBlockingQueue<>());

		broadcastTopics = new ConcurrentHashMap<>();
		broadcastTopics.put(Names.TICK_BROADCAST, new CopyOnWriteArrayList<>());
		broadcastTopics.put(Names.TERMINATE_ALL_BROADCAST, new CopyOnWriteArrayList<>());

		personalEventQueues = new ConcurrentHashMap<>();
		personalBroadcastQueues = new ConcurrentHashMap<>();
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
		eventTopics.get(type.getName()).add(m);
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
			case Names.MISSION_RECEIVED_EVENT:
				((MissionReceivedEvent) e).resolveFuture((Report) result);
				break;

			case Names.AGENTS_AVAILABLE_EVENT:
				((AgentsAvailableEvent) e).resolveFut((Pair<List<Agent>, Integer>) result);
				break;

			case Names.GADGET_AVAILABLE_EVENT:
				((GadgetAvailableEvent) e).resolveFut((Pair<String,Integer>) result);
				break;

			case Names.SEND_THEM_AGENTS:
				((SendThemAgentsEvent) e).resolveFut((boolean) result);
				break;

			case Names.RELEASE_AGENTS_EVENT:
				((ReleaseAgentsEvent) e).resolveFut((boolean) result);
				break;
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) throws InterruptedException {
		try {
			if (b.getClass().getName().equals(Names.TICK_BROADCAST)) currentTime.incrementAndGet();

			for (Subscriber subscriber : broadcastTopics.get(b.getClass().getName()))
				if(b.getClass().getName().equals(Names.TICK_BROADCAST)) sendTimeBroadcast(subscriber);
				else sendBroadcastToSubscriber(b, subscriber);

		} catch(NullPointerException e){}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) throws InterruptedException {
		String type = e.getClass().getName();

		Subscriber receiver;

		if(eventTopics.get(type).isEmpty()) return null;

		receiver = eventTopics.get(type).take();
		eventTopics.get(type).put(receiver);

		sendEventToSubscriber(e, receiver);

		return getEventFuture(e);
	}

	@Override
	public void register(Subscriber m) {
			personalEventQueues.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(Subscriber m) {
		personalEventQueues.remove(m);

		for(BlockingQueue topic : eventTopics.values()) if(topic.contains(m)) topic.remove(m);
		for(List<Subscriber> topic : broadcastTopics.values()) if(topic.contains(m)) topic.remove(m);
	}

	@Override
	public Message awaitMessage(Subscriber m) {
		try {
			if(!Thread.currentThread().isInterrupted()) return personalEventQueues.get(m).take();

			else {
				if (personalEventQueues.get(m).isEmpty()) return null;
				else return personalEventQueues.get(m).take();
			}

		} catch (InterruptedException e) {
			return null;
		}
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

	private void sendEventToSubscriber(Message message, Subscriber subscriber) throws InterruptedException {
		personalEventQueues.get(subscriber).put(message);
	}

	private void sendBroadcastToSubscriber(Message message, Subscriber subscriber) throws InterruptedException {
		personalBroadcastQueues.get(subscriber).put(message);
	}

	private Message getNextMessage(Subscriber m) throws InterruptedException {
		Message output = null;

		if(!Thread.currentThread().isInterrupted()){
			if(!personalTimeQueues.get(m).isEmpty()) output = personalTimeQueues.get(m).take();
			else if(!personalBroadcastQueues.get(m).isEmpty()) output = personalBroadcastQueues.get(m).take();
			else output = personalEventQueues.get(m).take();
		}

		else {
			if (personalTimeQueues.get(m).isEmpty()) {
				if (personalBroadcastQueues.get(m).isEmpty()) {
					if (personalEventQueues.get(m).isEmpty()) output = null;
					else output = personalEventQueues.get(m).take();
				} else output = personalBroadcastQueues.get(m).take();
			} else output = personalTimeQueues.get(m).take();
		}

		return output;
	}

	private void sendTimeBroadcast(Subscriber m, Broadcast tickBC) throws InterruptedException { personalTimeQueues.get(m).put(tickBC);}
}


























