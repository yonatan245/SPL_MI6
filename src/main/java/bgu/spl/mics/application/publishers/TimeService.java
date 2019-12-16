package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.TickBroadcast;
import java.util.concurrent.TimeUnit;

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

	private static class TimeServiceHolder{
		private static TimeService instance= new TimeService();
	}
	private long CurrentTime;

	private long TimeTicks;
	private TimeUnit unit;
	private TimeService() {
		super("The One And Only TimeService");
		this.TimeTicks=TimeTicks;
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static TimeService getInstance() {
		return TimeServiceHolder.instance;
	}

	public void SetTimeTicks(int TimeTicks){
		this.TimeTicks=TimeTicks;
	}

	@Override
	protected void initialize() {
	CurrentTime=0;
	}

	@Override
	public void run() {
	while(CurrentTime<TimeTicks){
		CurrentTime=CurrentTime+1;
		Broadcast toSend= new TickBroadcast(CurrentTime);
		this.getSimplePublisher().sendBroadcast(toSend);
		try {
			sleep(unit.toMillis(100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	}

}
