package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.GadgetAvailableEvent;
import bgu.spl.mics.application.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import org.javatuples.Pair;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

	private long CurrentTime;


	public Q() {
		super("I'm not fond of the actor Ben Whishaw");
	}

	@Override
	protected void initialize() {
		Callback<TickBroadcast> CBTB= c -> CurrentTime = c.getCurrentTime();
		Callback<GadgetAvailableEvent> CBGAE = c -> {
		if(Inventory.getInstance().getItem(c.getGadget())){
			Pair<String,Long> result = new Pair(c.getGadget(),CurrentTime);
			complete(c,result);
		}
		else{
			complete(c,null);
		}
		};
		subscribeBroadcast(TickBroadcast.class, CBTB);
		subscribeEvent(GadgetAvailableEvent.class,CBGAE);
	}

}
