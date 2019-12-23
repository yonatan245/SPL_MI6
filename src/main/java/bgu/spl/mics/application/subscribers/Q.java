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

	private AtomicLong currentTime;

	public Q() {
		super("I'm not fond of the actor Ben Whishaw");
		currentTime = new AtomicLong(0);
	}

	@Override
	protected void initialize() {
		Thread.currentThread().setName(getName());
		MessageBrokerImpl.getInstance().register(this);
		Callback<TickBroadcast> CBTickBroadcast= c -> currentTime.set(c.getCurrentTime());
		Callback<GadgetAvailableEvent> CBGadgetAvailableEvent = call -> {
			if(Inventory.getInstance().getItem(call.getGadget())){
			Pair<String,Long> result = new Pair(call.getGadget(),currentTime);
			complete(call,result);
			}
			else{
				complete(call,null);
			}
		};
		this.subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
			@Override
			public void call(TerminateAllBroadcast c) throws InterruptedException, ClassNotFoundException {
				terminate();
			}
		});
		subscribeBroadcast(TickBroadcast.class, CBTickBroadcast);
		subscribeEvent(GadgetAvailableEvent.class,CBGadgetAvailableEvent);
	}

}
