package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.*;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.javatuples.Pair;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	private Integer MoneyPennyID;
	private AtomicLong currentTime;


	public Moneypenny(Integer MPID) {
		super("Moneypenny"+MPID);
		MoneyPennyID=MPID;
		currentTime = new AtomicLong(0);
	}

	@Override
	protected void initialize() {
		Thread.currentThread().setName(getName());
		MessageBrokerImpl.getInstance().register(this);
		Callback<TickBroadcast> CBTB= c -> currentTime.set(c.getCurrentTime());
		Callback<AgentsAvailableEvent> CBAAE= c -> {
			if(Squad.getInstance().getAgents(c.getSerialAgentsNumbers())){
				Pair<List<String>,Integer> result = new Pair(Squad.getInstance().getAgentsNames(c.getSerialAgentsNumbers()), MoneyPennyID);
				complete(c,result);			}
			else
				complete(c,null);
		};
		Callback<ReleaseAgentsEvent> CBRAE = c -> {
			Squad.getInstance().releaseAgents(c.getSerialAgentsNumbers());
			complete(c,true);
		};
		Callback<SendThemAgentsEvent> CBSTAE = c-> {
			Squad.getInstance().sendAgents(c.getSerialAgentsNumbers(), c.getDuration());
			complete(c, true);
		};
		this.subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
			@Override
			public void call(TerminateAllBroadcast c) throws InterruptedException, ClassNotFoundException {
				terminate();
			}
		});
		subscribeBroadcast(TickBroadcast.class, CBTB);
		subscribeEvent(AgentsAvailableEvent.class,CBAAE);
		subscribeEvent(ReleaseAgentsEvent.class,CBRAE);
		subscribeEvent(SendThemAgentsEvent.class,CBSTAE);
	}

}
