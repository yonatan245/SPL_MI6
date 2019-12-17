package bgu.spl.mics;

import bgu.spl.mics.application.*;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Report;
import jdk.internal.net.http.common.Pair;

import java.util.*;
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

	//Constructor
	public static class MessageBrokerImplHolder{
		static private MessageBroker instance = new MessageBrokerImpl();

		public static MessageBroker getInstance(){ return instance;}
	}
	private MessageBrokerImpl(){

		eventTopics = new HashMap<>();
		eventTopics.put(MissionReceivedEvent.class.getName(), new EventTopic());
		eventTopics.put(AgentsAvailableEvent.class.getName(), new EventTopic());
		eventTopics.put(GadgetAvailableEvent.class.getName(), new EventTopic());
		eventTopics.put(ReleaseAgentsEvent.class.getName(), new EventTopic());
		eventTopics.put(SendThemAgentsEvent.class.getName(), new EventTopic());

		broadcastTopics = new HashMap<>();
		broadcastTopics.put(TickBroadcast.class.getName(), new ArrayList<>());

		personalQueues = new HashMap<>();
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
		assureEventType(type);
		eventTopics.get(type.getName()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		assureBroadcastType(type);
		broadcastTopics.get(type.getName()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		String whichEvent = e.getClass().getName();
		switch (whichEvent) {
			case "bgu.spl.mics.MissionReceivedEvent":
				((MissionReceivedEvent) e).resolveFuture((Report) result);
			case "bgu.spl.mics.AgentsAvailableEvent":
				((AgentsAvailableEvent) e).resolveFut((Pair<List<Agent>, Long>) result);
			case "bgu.spl.mics.GadgetAvailableEvent":
				((GadgetAvailableEvent) e).resolveFut((String) result);
			case "bgu.spl.mics.SendThemAgentsEvent":
				((SendThemAgentsEvent) e).resolveFut((boolean) result);
			case "bgu.spl.mics.ReleaseAgentsEvent":
				((ReleaseAgentsEvent) e).resolveFut((boolean) result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		assureBroadcastType(b.getClass());
		for(Subscriber subscriber : broadcastTopics.get(b.getClass().getName()))
			personalQueues.get(subscriber).add(b);
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) throws ClassNotFoundException {
		String type = e.getClass().getName();
		Future<T> future = null;

		if(eventTopics.containsKey(type) && !eventTopics.get(type).isEmpty()){
			Subscriber receiver = eventTopics.get(type).getNextSubscriber();
			personalQueues.get(receiver).add(e);
			future = getEventFuture(e);
		}

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
		// TODO Auto-generated method stub
		return null;
	}

	private <T> void assureEventType(Class<? extends Event> type) {
		if(!eventTopics.containsKey(type.getName())) eventTopics.put(type.getName(), new EventTopic());
	}

	private <T> void assureBroadcastType(Class<? extends Broadcast> type) {
		if(!broadcastTopics.containsKey(type)) broadcastTopics.put(type.getName(), new ArrayList<>());
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



}
