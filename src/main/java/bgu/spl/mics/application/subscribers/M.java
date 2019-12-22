package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;
import org.javatuples.Pair;


import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {

	private Integer MID;
	private MissionInfo CurrentMission;
	private TimeUnit unit;
	private AtomicLong currentTime;


	public M(Integer NMID) {
		super("M"+NMID);
		MID=NMID;
		currentTime = new AtomicLong(0);
	}

	@Override
	protected void initialize() {
		Thread.currentThread().setName(getName());
		MessageBrokerImpl.getInstance().register(this);
		Callback<TickBroadcast> CBTB= c -> currentTime.set(c.getCurrentTime());
		Callback<MissionReceivedEvent> CBMRE= c -> {
			CurrentMission=c.getMission();
			c.setStatus("IN_PROGRESS");
		Future<Pair<List<String>,Integer>> agentsAndMPID = getSimplePublisher().sendEvent(new AgentsAvailableEvent<>(CurrentMission.getSerialAgentsNumbers()));
			if(agentsAndMPID.get(CurrentMission.getTimeExpired(),unit)==null){
				c.setStatus("ABORTED");
				Diary.getInstance().incrementTotal();
			}
			else{
				Future<Pair<String,Long>> GadgetAndQTime = getSimplePublisher().sendEvent(new GadgetAvailableEvent<>(CurrentMission.getGadget()));
				if(GadgetAndQTime.get()==null){
					c.setStatus("ABORTED");
					Diary.getInstance().incrementTotal();
				}
				else if(currentTime.get()>CurrentMission.getTimeExpired()){
					c.setStatus("ABORTED");
					Future<Boolean> Release = getSimplePublisher().sendEvent(new ReleaseAgentsEvent<>(CurrentMission.getSerialAgentsNumbers()));
					Diary.getInstance().incrementTotal();
				}
				else{
					Future<Boolean> SendThem = getSimplePublisher().sendEvent(new SendThemAgentsEvent<>(CurrentMission.getSerialAgentsNumbers(),CurrentMission.getDuration()));
					Report toAdd = new Report(CurrentMission.getMissionName(), MID, agentsAndMPID.get().getValue1(), CurrentMission.getSerialAgentsNumbers(), agentsAndMPID.get().getValue0(), CurrentMission.getGadget(), CurrentMission.getTimeIssued(), GadgetAndQTime.get().getValue1(), currentTime.get());
					Diary.getInstance().addReport(toAdd);
					c.setStatus("COMPLETED");
				}

				}
			};
		subscribeBroadcast(TickBroadcast.class,CBTB);
		subscribeEvent(MissionReceivedEvent.class,CBMRE);
		this.subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
			@Override
			public void call(TerminateAllBroadcast c) throws InterruptedException, ClassNotFoundException {
				Diary.getInstance().printToFile(Names.OUTPUT_DIARY);
				terminate();
			}
		});
	}

}
