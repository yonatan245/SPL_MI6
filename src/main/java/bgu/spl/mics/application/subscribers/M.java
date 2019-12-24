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
//			System.out.println(Thread.currentThread().getName() +", tick broadcast with time: " +c.getCurrentTime());

			if (currentTime.get() < c.getCurrentTime()) currentTime.set(c.getCurrentTime());

			if(c.getCurrentTime() >= timeTicks) terminate();
		};

		Callback<MissionReceivedEvent> missionReceivedCallBack = c -> {
			Diary.getInstance().incrementTotal();

			if(c.getTimeIssued()>=timeTicks) terminate();
			if (currentTime.get() < c.getTimeIssued()) currentTime.set(c.getTimeIssued());

			currentMission = c.getMission();
			if(currentMission.getTimeExpired() < timeTicks) currentMission.setTimeExpired(timeTicks);
			boolean isAborted = false;

			if (currentTime.get() < currentMission.getTimeIssued())
				currentTime.set(currentMission.getTimeIssued());

			int remainingTime = currentMission.getTimeExpired() - currentTime.get() - currentMission.getDuration();

			//Check Moneypenny for agents availability
			Event checkAgents = new AgentsAvailableEvent<>(currentMission.getSerialAgentsNumbers(), currentTime.get());
			Future <Pair <List <String>, Integer>> agentsAndMPIDFuture = getSimplePublisher().sendEvent(checkAgents);

			Pair <List <String>, Integer> agentsAndMPID = agentsAndMPIDFuture.get(remainingTime, unit);

			if (agentsAndMPID == null) isAborted = true;

			//Check Q for gadget availability
			Event checkGadget =  new GadgetAvailableEvent<>(currentMission.getGadget(), currentTime.get());
			Future <Pair <String, AtomicInteger>> gadgetAndQTimeFuture = getSimplePublisher().sendEvent(checkGadget);

			Pair<String, AtomicInteger> gadgetAndQTime = null;
			if(!isAborted) gadgetAndQTime = gadgetAndQTimeFuture.get(remainingTime, unit);

			if(!isAborted) {
				if (gadgetAndQTime == null)	isAborted = true;
				else remainingTime = remainingTime - (gadgetAndQTime.getValue1().get() - currentTime.get());
			}

			if(!isAborted && remainingTime <= 0) isAborted = true;

			//If the resources are available for the mission, send them agents and add a report to Diary
			if(!isAborted) {
				Event sendThemAgents = new SendThemAgentsEvent(currentMission.getSerialAgentsNumbers(), currentMission.getDuration(), currentTime.get());
				getSimplePublisher().sendEvent(sendThemAgents);

				Report toAdd = new Report(
						currentMission.getMissionName(),
						MID,
						agentsAndMPID.getValue1(),
						currentMission.getSerialAgentsNumbers(),
						agentsAndMPID.getValue0(),
						currentMission.getGadget(),
						currentMission.getTimeIssued(),
						gadgetAndQTime.getValue1().get(),
						currentTime.get());

				Diary.getInstance().addReport(toAdd);
			}

			else{
				Event releaseAgents = new ReleaseAgentsEvent(currentMission.getSerialAgentsNumbers(), currentTime.get());
				getSimplePublisher().sendEvent(releaseAgents);
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
