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

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {

	//Fields
	private Integer MID;
	private MissionInfo currentMission;
	private TimeUnit unit;
	private AtomicInteger currentTime;
	private int timeTicks;

	//Constructor
	public M(Integer NMID, int timeTicks) {
		super("M"+NMID);
		MID=NMID;
		currentTime = new AtomicInteger(0);
		unit = TimeUnit.MILLISECONDS;
		this.timeTicks = timeTicks;
	}

	//Methods
	@Override
	protected void initialize() {
		Thread.currentThread().setName(getName());
		MessageBrokerImpl.getInstance().register(this);

		Callback<TickBroadcast> tickBroadcastCallBack = c -> {

			if (currentTime.get() < c.getCurrentTime()) currentTime.set(c.getCurrentTime());

			if(c.getCurrentTime() >= timeTicks) terminate();
		};

		Callback<MissionReceivedEvent> missionReceivedCallBack = c -> {
			Diary.getInstance().incrementTotal();

			if (c.getTimeIssued() >= timeTicks) terminate();
			if (currentTime.get() < c.getTimeIssued()) currentTime.set(c.getTimeIssued());

			currentMission = c.getMission();
			if (currentMission.getTimeExpired() < timeTicks) currentMission.setTimeExpired(timeTicks);
			boolean isAborted = false;
			Boolean needToReleaseAgents = false;
			boolean init = true;
			Pair<List<String>, Integer> agentsAndMPID = null;
			Pair<String, AtomicInteger> gadgetAndQTime = null;

			if (currentTime.get() < currentMission.getTimeIssued())
				currentTime.set(currentMission.getTimeIssued());

			while(init) {
				init = false;

				int remainingTime = currentMission.getTimeExpired() - currentTime.get() - currentMission.getDuration();
				if (remainingTime <= 0){
					isAborted = true;
					break;
				}

				//Check Moneypenny for agents availability
				agentsAndMPID = getAgentsAndMPid(remainingTime);
				if (agentsAndMPID == null){
					isAborted = true;
					break;
				}
				else needToReleaseAgents = true;

				//Check Q for gadget availability
				gadgetAndQTime = getGadgetAndQtime(remainingTime);

				if (gadgetAndQTime == null){
					isAborted = true;
					break;
				}
				else remainingTime = remainingTime - (gadgetAndQTime.getValue1().get() - currentTime.get());


				//Check if there is still enough time for the mission execution
				if (remainingTime <= 1){
					isAborted = true;
					break;
				}
			}

			if(!isAborted) {
				sendThemAgents();
				reportMission(agentsAndMPID.getValue1(), agentsAndMPID.getValue0(), gadgetAndQTime.getValue1().get());
			}
			else{
				if(needToReleaseAgents) releaseMissionAgents(currentMission.getSerialAgentsNumbers(), currentTime.get(), currentMission.getMissionName());
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

	private void releaseMissionAgents(List<String> agentsToRelease, Integer time, String missionName) throws InterruptedException, ClassNotFoundException {
		Event releaseAgents = new ReleaseAgentsEvent(agentsToRelease, time);
		getSimplePublisher().sendEvent(releaseAgents);
	}

	private Pair<List<String>, Integer> getAgentsAndMPid(int remainingTime) throws InterruptedException, ClassNotFoundException {
		Event checkAgents = new AgentsAvailableEvent<>(currentMission.getSerialAgentsNumbers(), currentTime.get());
		Future<Pair<List<String>, Integer>> agentsAndMPIDFuture = getSimplePublisher().sendEvent(checkAgents);

		Pair<List<String>, Integer> agentsAndMPID = agentsAndMPIDFuture.get(remainingTime, unit);

		return agentsAndMPID;
	}

	private Pair<String, AtomicInteger> getGadgetAndQtime(int remainingTime) throws InterruptedException, ClassNotFoundException {
		Event checkGadget = new GadgetAvailableEvent<>(currentMission.getGadget(), currentTime.get());
		Future<Pair<String, AtomicInteger>> gadgetAndQTimeFuture = getSimplePublisher().sendEvent(checkGadget);

		Pair<String, AtomicInteger> gadgetAndQTime = gadgetAndQTimeFuture.get(remainingTime, unit);

		return gadgetAndQTime;
	}

	private void sendThemAgents() throws InterruptedException, ClassNotFoundException {
		Event sendThemAgents = new SendThemAgentsEvent(currentMission.getSerialAgentsNumbers(), currentMission.getDuration(), currentTime.get());
		getSimplePublisher().sendEvent(sendThemAgents);
	}

	private void reportMission(Integer moneypennyId, List<String> agentNames, Integer qTime) {
		Report toAdd = new Report(
				currentMission.getMissionName(),
				MID,
				moneypennyId,
				currentMission.getSerialAgentsNumbers(),
				agentNames,
				currentMission.getGadget(),
				currentMission.getTimeIssued(),
				qTime,
				currentTime.get());

		Diary.getInstance().addReport(toAdd);
	}

}
