package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.TerminateAllBroadcast;
import bgu.spl.mics.application.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {


	private AtomicInteger currentTime;
	private int TimeTicks;
	private Timer timer;


	public TimeService(int TimeTicks) {
		super("The One And Only TimeService");
		timer = new Timer("The One And Only TimeService");
		currentTime = new AtomicInteger(0);
		this.TimeTicks = TimeTicks;
	}

	/**
	 * Retrieves the single instance of this class.
	 */


	@Override
	protected void initialize() {
	}

	@Override
	public void run() {
		timer.scheduleAtFixedRate(getNewTimerTask(), 0, 100);
		while(currentTime.get() < TimeTicks);

		TimeService.super.getSimplePublisher().sendBroadcast(new TerminateAllBroadcast());
		System.out.println("Termination has started");

		timer.cancel();
	}

	private TimerTask getNewTimerTask(){
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if(currentTime.get() < TimeTicks ) {
					Broadcast toSend = new TickBroadcast(currentTime.getAndIncrement());
					System.out.println("Time Service - current time: " +currentTime.get());
					TimeService.super.getSimplePublisher().sendBroadcast(toSend);
				}
			}
		};

		return task;
	}

}
