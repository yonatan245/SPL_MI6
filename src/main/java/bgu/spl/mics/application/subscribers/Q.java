package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Names;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.GadgetAvailableEvent;
import bgu.spl.mics.application.TerminateAllBroadcast;
import bgu.spl.mics.application.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import org.javatuples.Pair;

import javax.naming.Name;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

	private AtomicInteger currentTime;
	private int timeTicks;

	public Q(int timeTicks) {
		super("I'm not fond of the actor Ben Whishaw");
		currentTime = new AtomicInteger(0);
		this.timeTicks = timeTicks;
	}

	@Override
	protected void initialize() {
		Thread.currentThread().setName(getName());
		MessageBrokerImpl.getInstance().register(this);

		Callback<TickBroadcast> tickBroadcastCallback= c -> {

			if(c.getCurrentTime()>=timeTicks) terminate();
			if(currentTime.get() < c.getCurrentTime())
				currentTime.set(c.getCurrentTime());
		};

		Callback<GadgetAvailableEvent> gadgetAvailableEventCallback = call -> {
			try {
				if(call.getMTime()>=timeTicks) terminate();
				if (currentTime.get() < call.getMTime()) currentTime.set(call.getMTime());

				if (Inventory.getInstance().getItem(call.getGadget())) {
					Pair<String, Integer> result = new Pair(call.getGadget(), currentTime);
					complete(call, result);
				} else {
					complete(call, null);
				}
			} catch(NullPointerException e) {terminate();}
		};

		this.subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
			@Override
			public void call(TerminateAllBroadcast c) throws InterruptedException, ClassNotFoundException {
				terminate();
			}
		});
		subscribeBroadcast(TickBroadcast.class, tickBroadcastCallback);
		subscribeEvent(GadgetAvailableEvent.class,gadgetAvailableEventCallback);
	}

}
