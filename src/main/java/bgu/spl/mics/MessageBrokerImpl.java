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
	private ConcurrentMap<Subscriber, BlockingQueue<Message>> personalQueues;


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

		personalQueues = new ConcurrentHashMap<>();
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
				((SendThemAgentsEvent) e).resolveFut((Boolean) result);
				break;

			case Names.RELEASE_AGENTS_EVENT:
				((ReleaseAgentsEvent) e).resolveFut((Boolean) result);
				break;
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) throws InterruptedException {
		try {
			if (b.getClass().getName().equals(Names.TICK_BROADCAST)) currentTime.incrementAndGet();

			for (Subscriber subscriber : broadcastTopics.get(b.getClass().getName()))
				 sendMessageToSubscriber(b, subscriber);

		} catch(NullPointerException e){}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) throws InterruptedException {
		String type = e.getClass().getName();

		Subscriber receiver;

		if(eventTopics.get(type).isEmpty()) return null;

		receiver = eventTopics.get(type).take();
		eventTopics.get(type).put(receiver);

		sendMessageToSubscriber(e, receiver);

		return getEventFuture(e);
	}

	@Override
	public void register(Subscriber m) {
		personalQueues.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(Subscriber m) {
		personalQueues.remove(m);

		for(BlockingQueue topic : eventTopics.values()) if(topic.contains(m)) topic.remove(m);
		for(List<Subscriber> topic : broadcastTopics.values()) if(topic.contains(m)) topic.remove(m);
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		Message output = null;

		if (!Thread.currentThread().isInterrupted()) {
			output = personalQueues.get(m).take();

		} else	if (!personalQueues.get(m).isEmpty()) output = personalQueues.get(m).take();

		return output;
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

	private void sendMessageToSubscriber(Message message, Subscriber subscriber) throws InterruptedException {
		personalQueues.get(subscriber).put(message);
	}

}


























