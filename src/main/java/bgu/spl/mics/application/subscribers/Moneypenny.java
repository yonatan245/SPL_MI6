package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.*;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.javatuples.Pair;

import java.io.FileNotFoundException;
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
	private AtomicInteger currentTime;
	private int timeTicks;


	public Moneypenny(Integer MPID, int timeTicks) {
		super("Moneypenny"+MPID);
		MoneyPennyID=MPID;
		currentTime = new AtomicInteger(0);
		this.timeTicks = timeTicks;
	}

	@Override
	protected void initialize() {
		Thread.currentThread().setName(getName());
		MessageBrokerImpl.getInstance().register(this);

			Callback<TickBroadcast> CBTB = c -> {
				System.out.println(Thread.currentThread().getName() +", tick broadcast with time: " +c.getCurrentTime());
				if(c.getCurrentTime()>=timeTicks) terminate();

				if (currentTime.get() < c.getCurrentTime())
					currentTime.set(c.getCurrentTime());
			};

			Callback<AgentsAvailableEvent> CBAAE = c -> {
				try {
					if(c.getTime()>=timeTicks) terminate();
					if (currentTime.get() < c.getTime()) currentTime.set(c.getTime());

					if (Squad.getInstance().getAgents(c.getSerialAgentsNumbers())) {
						Pair<List<String>, Integer> result = new Pair(Squad.getInstance().getAgentsNames(c.getSerialAgentsNumbers()), MoneyPennyID);
						complete(c, result);
					} else
						complete(c, null);
				} catch (NullPointerException | InterruptedException e){terminate();}
			};

			Callback<ReleaseAgentsEvent> CBRAE = c -> {
				try {
					if(c.getTime()>=timeTicks) terminate();
					if (currentTime.get() < c.getTime()) currentTime.set(c.getTime());

					Squad.getInstance().releaseAgents(c.getSerialAgentsNumbers());
					complete(c, true);
				} catch(NullPointerException | InterruptedException e){terminate();}
			};

			Callback<SendThemAgentsEvent> CBSTAE = c -> {
				try {
					if(c.getTime()>=timeTicks) terminate();
					if (currentTime.get() < c.getTime()) currentTime.set(c.getTime());

					Squad.getInstance().sendAgents(c.getSerialAgentsNumbers(), c.getDuration());
					complete(c, true);
				} catch(NullPointerException | InterruptedException e){terminate();}
			};

		this.subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
			@Override
			public void call(TerminateAllBroadcast c) throws InterruptedException, ClassNotFoundException {
				terminate();
			}
		});

		subscribeBroadcast(TickBroadcast.class, CBTB);
		if (MoneyPennyID % 2 == 0) {
			subscribeEvent(AgentsAvailableEvent.class, CBAAE);
		}
		if (MoneyPennyID % 2 != 0) {
			subscribeEvent(ReleaseAgentsEvent.class, CBRAE);
			subscribeEvent(SendThemAgentsEvent.class, CBSTAE);
		}

	}
}
