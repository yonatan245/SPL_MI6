package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {

	//Fields
	private List<Report> reports;
	private int total;


	//Constructor
	private static class DiaryHolder{
		private static Diary instance = new Diary();

		public static Diary getInstance() {
			return instance;
		}
	}
	private Diary(){
		reports = new ArrayList<>();
		total = 0;
	}

	//Methods
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		return DiaryHolder.getInstance();
	}

	public List<Report> getReports() {
		return reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		reports.add(reportToAdd);
		total++;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
		//TODO: Implement this
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return total;
	}

	//TODO: change writing to json format
	private String reportToString(Report report){
		StringBuilder agentSerialNumbers = new StringBuilder("{");
		for(String serial : report.getAgentsSerialNumbersNumber()) agentSerialNumbers.append(serial).append(", ");
		agentSerialNumbers = new StringBuilder(agentSerialNumbers.substring(0, agentSerialNumbers.length() - 1));
		agentSerialNumbers.append("}");

		StringBuilder agentNames = new StringBuilder("{");
		for(String serial : report.getAgentsNames()) agentNames.append(serial).append(", ");
		agentNames = new StringBuilder(agentNames.substring(0, agentNames.length() - 1));
		agentNames.append("}");

		return "Mission name: " +report.getMissionName() +"\n"
				+"M id: " +report.getM() +"\n"
				+"MoneyPenny id: " +report.getMoneypenny() +"\n"
				+"Agent serial numbers: " +agentSerialNumbers +"\n"
				+"Agent names: " +agentNames +"\n"
				+"Gadget name: " +report.getGadgetName() +"\n"
				+"Time issued: " +report.getTimeIssued() +"\n"
				+"Time Created: " +report.getTimeCreated() +"\n"
				+"QTime: " +report.getQTime() +"\n";

	}
}
