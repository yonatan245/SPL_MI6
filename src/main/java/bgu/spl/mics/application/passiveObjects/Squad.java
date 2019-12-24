package bgu.spl.mics.application.passiveObjects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.*;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	//Fields
	private Map<String, Agent> agents;
	private Map<Agent, Semaphore> semaphoreMap;

	//Constructor
	private static class SquadHolder{
		private static Squad instance = new Squad();

		public static Squad getInstance() {
			return instance;
		}
	}
	private Squad(){
		agents = new HashMap<>();
		semaphoreMap = new HashMap<>();
	}

	//Methods
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return SquadHolder.getInstance();
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		for(Agent agent : agents){
			String serialNumber = agent.getSerialNumber();
			this.agents.put(serialNumber, agent);
			this.semaphoreMap.put(agent, new Semaphore(1,true));
		}
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials) throws InterruptedException {
		Collections.sort(serials);

		for(String serial : serials){
			Agent toRelease = agents.get(serial);
			toRelease.release();

			semaphoreMap.get(toRelease).release();
			System.out.println(Thread.currentThread().getName() +" has released agent " +toRelease.getSerialNumber());

		}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time) throws InterruptedException {
		Collections.sort(serials);
		Thread.currentThread().sleep(time * 100);
		releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials) throws InterruptedException {

		//TODO: fix agents acquiring. release agents can release them from another mission's job. we can create a map that says which mission is acquiring it.

		List<String> acquiredAgents = new ArrayList<>();
		boolean aborted = false;
		Collections.sort(serials);
		Agent currentAgent;


		for(String serial : serials){
			currentAgent = agents.get(serial);

			if(currentAgent == null){
				aborted = true;
				break;
			}
			else {
				semaphoreMap.get(currentAgent).acquire();
				System.out.println(Thread.currentThread().getName() +" has acquired agent " +currentAgent.getSerialNumber());
				currentAgent.acquire();
				acquiredAgents.add(serial);
			}
		}

		if(aborted) releaseAgents(acquiredAgents);

		return !aborted;
	}

    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials) throws InterruptedException {

		List<String> agentNames = new ArrayList<>();

		Collections.sort(serials);
		Agent currentAgent;

        for(String serial : serials){
        	currentAgent = agents.get(serial);
			if(currentAgent != null) {
				agentNames.add(currentAgent.getName());
			}
		}

        return agentNames;
    }

}
