package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive data-object representing a delivery vehicle of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Report {

	//Fields
	private String missionName;
	private int mId;
	private int moneyPennyId;
	private List<String> agentsSerialNumbers;
	private List<String> agentsNames;
	private String gadgetName;
	private long timeIssued;
	private long QTime;
	private long timeCreated;

	//Constructors
	public Report(String missionName, int mId, int moneyPennyId, List<String> agentsSerialNumbers,
				  List<String> agentsNames, String gadgetName, int timeIssued, long QTime, long timeCreated){
		this.missionName = missionName;
		this.mId = mId;
		this.moneyPennyId = moneyPennyId;
		this.agentsSerialNumbers = agentsSerialNumbers;
		this.agentsNames = agentsNames;
		this.gadgetName = gadgetName;
		this.timeIssued = timeIssued;
		this.QTime = QTime;
		this.timeCreated = timeCreated;
	}

	public Report(){
		this.missionName = "";
		this.mId = 0;
		this.moneyPennyId = 0;
		this.agentsSerialNumbers = new ArrayList<>();
		this.agentsNames = new ArrayList<>();
		this.gadgetName = "";
		this.timeIssued = 0;
		this.QTime = 0;
		this.timeCreated = 0;
	}

	//Methods
	/**
     * Retrieves the mission name.
     */
	public String getMissionName() {
		return missionName;
	}

	/**
	 * Sets the mission name.
	 */
	public void setMissionName(String missionName) {
		this.missionName = missionName;
	}

	/**
	 * Retrieves the M's id.
	 */
	public int getM() {
		return  mId;
	}

	/**
	 * Sets the M's id.
	 */
	public void setM(int m) {
		mId = m;
	}

	/**
	 * Retrieves the Moneypenny's id.
	 */
	public int getMoneypenny() {
		return moneyPennyId;
	}

	/**
	 * Sets the Moneypenny's id.
	 */
	public void setMoneypenny(int moneypenny) {
		moneyPennyId = moneypenny;
	}

	/**
	 * Retrieves the serial numbers of the agents.
	 * <p>
	 * @return The serial numbers of the agents.
	 */
	public List<String> getAgentsSerialNumbersNumber() {
		return agentsSerialNumbers;
	}

	/**
	 * Sets the serial numbers of the agents.
	 */
	public void setAgentsSerialNumbers(List<String> agentsSerialNumbers) {
		this.agentsSerialNumbers = agentsSerialNumbers;
	}

	/**
	 * Retrieves the agents names.
	 * <p>
	 * @return The agents names.
	 */
	public List<String> getAgentsNames() {
		return agentsNames;
	}

	/**
	 * Sets the agents names.
	 */
	public void setAgentsNames(List<String> agentsNames) {
		this.agentsNames = agentsNames;
	}

	/**
	 * Retrieves the name of the gadget.
	 * <p>
	 * @return the name of the gadget.
	 */
	public String getGadgetName() {
		return gadgetName;
	}

	/**
	 * Sets the name of the gadget.
	 */
	public void setGadgetName(String gadgetName) {
		this.gadgetName = gadgetName;
	}

	/**
	 * Retrieves the time-tick in which Q Received the GadgetAvailableEvent for that mission.
	 */
	public long getQTime() {
		return QTime;
	}

	/**
	 * Sets the time-tick in which Q Received the GadgetAvailableEvent for that mission.
	 */
	public void setQTime(long qTime) {
		QTime = qTime;
	}

	/**
	 * Retrieves the time when the mission was sent by an Intelligence Publisher.
	 */
	public long getTimeIssued() {
		return timeIssued;
	}

	/**
	 * Sets the time when the mission was sent by an Intelligence Publisher.
	 */
	public void setTimeIssued(int timeIssued) {
		this.timeIssued = timeIssued;
	}

	/**
	 * Retrieves the time-tick when the report has been created.
	 */
	public long getTimeCreated() {
		return timeCreated;
	}

	/**
	 * Sets the time-tick when the report has been created.
	 */
	public void setTimeCreated(long timeCreated) {
		this.timeCreated = timeCreated;
	}
}
