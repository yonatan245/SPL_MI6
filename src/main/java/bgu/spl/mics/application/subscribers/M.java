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
	private MissionInfo currentMission;
	private TimeUnit unit;
	private AtomicInteger currentTime;
	private int timeTicks;


	public M(Integer NMID, int timeTicks) {
		super("M"+NMID);
		MID=NMID;
		currentTime = new AtomicInteger(0);
		unit = TimeUnit.MILLISECONDS;
		this.timeTicks = timeTicks;
	}

	@Override
	protected void initialize() {
		Thread.currentThread().setName(getName());
		MessageBrokerImpl.getInstance().register(this);

		Callback<TickBroadcast> tickBroadcastCallBack = c -> {
			System.out.println(Thread.currentThread().getName() +", tick broadcast with time: " +c.getCurrentTime());

			if (currentTime.get() < c.getCurrentTime()) currentTime.set(c.getCurrentTime());

			if(c.getCurrentTime() >= timeTicks) terminate();
		};

		Callback<MissionReceivedEvent> missionReceivedCallBack= c -> {
				Diary.getInstance().incrementTotal();
			if(c.getTimeIssued()>=timeTicks) terminate();
				if (currentTime.get() < c.getTimeIssued()) currentTime.set(c.getTimeIssued());

				currentMission = c.getMission();
				c.setStatus("IN_PROGRESS");

				if (currentTime.get() < currentMission.getTimeIssued())
					currentTime.set(currentMission.getTimeIssued());

				Event agentsAndMoneypennyIDEvent = new AgentsAvailableEvent<>(currentMission.getSerialAgentsNumbers(), currentTime.get());
				Future<Pair<List<String>, Integer>> agentsAndMPID = getSimplePublisher().sendEvent(agentsAndMoneypennyIDEvent);

				if (agentsAndMPID.get(currentMission.getTimeExpired(), unit) == null) {
					c.setStatus("ABORTED");
				} else {
					Future<Pair<String, AtomicInteger>> GadgetAndQTime = getSimplePublisher().sendEvent(new GadgetAvailableEvent<>(currentMission.getGadget(), currentTime.get()));
					if (GadgetAndQTime.get(currentMission.getTimeExpired(), unit) == null) {
						c.setStatus("ABORTED");
					} else if (currentTime.get() > currentMission.getTimeExpired()) {
						c.setStatus("ABORTED");
						getSimplePublisher().sendEvent(new ReleaseAgentsEvent<>(currentMission.getSerialAgentsNumbers(), currentTime.get()));
					} else {
						getSimplePublisher().sendEvent(new SendThemAgentsEvent<>(currentMission.getSerialAgentsNumbers(), currentMission.getDuration(), currentTime.get()));
						Report toAdd = new Report(currentMission.getMissionName(),
								MID,
								agentsAndMPID.get().getValue1(),
								currentMission.getSerialAgentsNumbers(),
								agentsAndMPID.get().getValue0(),
								currentMission.getGadget(),
								currentMission.getTimeIssued(),
								GadgetAndQTime.get().getValue1().get(),
								currentTime.get());
						Diary.getInstance().addReport(toAdd);
						c.setStatus("COMPLETED");
					}
				}
		};

		subscribeBroadcast(TickBroadcast.class,tickBroadcastCallBack);
		subscribeEvent(MissionReceivedEvent.class,missionReceivedCallBack);
		this.subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
			@Override
			public void call(TerminateAllBroadcast c) throws InterruptedException, ClassNotFoundException {
				terminate();
			}
		});
	}

}
