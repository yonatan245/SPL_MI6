package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Event;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.MissionReceivedEvent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

/**
 * A Publisher only.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Publisher {
	private MissionInfo[] missions;

	public Intelligence(String name,MissionInfo[] missions) {
		super(name);
		this.missions=missions;
	}

	@Override
	protected void initialize() {
		for(MissionInfo m:missions) {
			Event<MissionInfo> toPublish = new MissionReceivedEvent(m);
			this.getSimplePublisher().sendEvent(toPublish);
		}
	}

	@Override
	public void run() {
		// TODO Implement this
	}

}
