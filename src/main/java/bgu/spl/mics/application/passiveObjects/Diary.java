package bgu.spl.mics.application.passiveObjects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.*;


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
	private AtomicInteger total;
	private AtomicBoolean wasPrinted;


	//Constructor
	private static class DiaryHolder{
		private static Diary instance = new Diary();

		public static Diary getInstance() {
			return instance;
		}
	}
	private Diary() {
		reports = new ArrayList<>();
		total = new AtomicInteger(0);
	}
	//Methods
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		return DiaryHolder.instance;
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
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){

		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).setPrettyPrinting().create();
		String diaryJson = gson.toJson(Diary.getInstance());

		try(Writer writer = new FileWriter(filename)){
			writer.write(diaryJson);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return total.get();
	}

	private JSONObject reportToJSON(Report report){
		JSONObject jsonReport = new JSONObject();

		JSONArray agentSerialNumbers = new JSONArray();
		agentSerialNumbers.addAll(report.getAgentsSerialNumbersNumber());
		JSONArray agentNames = new JSONArray();
		agentNames.addAll(report.getAgentsSerialNumbersNumber());

		jsonReport.put("Mission name", report.getMissionName());
		jsonReport.put("M", report.getM());
		jsonReport.put("Money Penny", report.getMoneypenny());
		jsonReport.put("Agents serial numbers", agentSerialNumbers);
		jsonReport.put("Agent names", agentNames);
		jsonReport.put("Gadget name", report.getGadgetName());
		jsonReport.put("Time issued", report.getTimeIssued());
		jsonReport.put("Q time", report.getQTime());
		jsonReport.put("Time created", report.getTimeCreated());

		return jsonReport;
	}
	public void incrementTotal(){
		total.getAndIncrement();
	}

}
