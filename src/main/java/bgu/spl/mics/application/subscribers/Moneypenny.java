package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.AgentsAvailableEvent;
import bgu.spl.mics.application.ReleaseAgentsEvent;
import bgu.spl.mics.application.SendThemAgentsEvent;
import bgu.spl.mics.application.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.javatuples.Pair;

import java.util.List;


/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	private long CurrentTime;
	private Integer MoneyPennyID;

	public Moneypenny(Integer MPID) {
		super("Moneypenny"+MPID);
		MoneyPennyID=MPID;
	}

	@Override
	protected void initialize() {
		Callback<TickBroadcast> CBTB= c -> CurrentTime = c.getCurrentTime();
		Callback<AgentsAvailableEvent> CBAAE= c -> {
			if(Squad.getInstance().getAgents(c.getSerialAgentsNumbers())){
				Pair<List<String>,Long> result = new Pair(Squad.getInstance().getAgentsNames(c.getSerialAgentsNumbers()), MoneyPennyID);
				complete(c,result);			}
			else
				complete(c,null);
		};
		Callback<ReleaseAgentsEvent> CBRAE = c -> {
			Squad.getInstance().releaseAgents(c.getSerialAgentsNumbers());
			complete(c,true);
		};
		Callback<SendThemAgentsEvent> CBSTAE = new Callback<SendThemAgentsEvent>() {
			@Override
			public void call(SendThemAgentsEvent c) throws InterruptedException {
				Squad.getInstance().sendAgents(c.getSerialAgentsNumbers(),c.getDuration());
				complete(c,true);
			}
		};
		subscribeBroadcast(TickBroadcast.class, CBTB);
		subscribeEvent(AgentsAvailableEvent.class,CBAAE);
		subscribeEvent(ReleaseAgentsEvent.class,CBRAE);
		subscribeEvent(SendThemAgentsEvent.class,CBSTAE);
	}

}
