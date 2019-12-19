package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {

        String filePath = "src/input201 - 2.json";
        Gson gson = new Gson();
        List<Thread> threads = new ArrayList<>();

        try {
            JsonReader reader = new JsonReader(new FileReader(filePath));
            JsonElement e = JsonParser.parseReader(reader);

            //Initialize Inventory
            Inventory inventory = Inventory.getInstance();
            JsonArray inventoryJson = e.getAsJsonObject().get("inventory").getAsJsonArray();
            String[] gadgetsToLoad = new String[inventoryJson.size()];
            int i = 0;

            for(JsonElement gadgetJson : inventoryJson){
                gadgetsToLoad[i] = gadgetJson.toString();
                i++;
            }

            inventory.load(gadgetsToLoad);

            //Initialize Squad
            Squad squad = Squad.getInstance();
            JsonArray squadJson = e.getAsJsonObject().get("squad").getAsJsonArray();
            Agent[] agentsToLoad = new Agent[squadJson.size()];
            i = 0;

            for(JsonElement agentJson : squadJson){
                Agent toAdd = gson.fromJson(agentJson, Agent.class);
                agentsToLoad[i] = toAdd;
                i++;
            }

            squad.load(agentsToLoad);

            //Initialize Services
            JsonObject services = e.getAsJsonObject().get("services").getAsJsonObject();

            //Initialize M
            int numberOfMs = services.get("M").getAsInt();
            for(i = 0 ; i <= numberOfMs ; i++) threads.add(new Thread(new M(i)));

            //Initialize Money Penny
            int numberOfMoneyPennies = services.get("Moneypenny").getAsInt();
            for(i = 0 ; i <= numberOfMoneyPennies ; i++) threads.add(new Thread(new Moneypenny(i+1)));

            //Initialize Intelligence
            JsonArray intelligenceJson = services.get("intelligence").getAsJsonArray();
            Iterator<JsonElement> intelligenceIter = intelligenceJson.iterator();
            i = 0;

            //iterating on all of the intelligences
            while(intelligenceIter.hasNext()){
                JsonArray missionsJson = (JsonArray) intelligenceIter.next();
                Iterator<JsonElement> missionsIter = missionsJson.iterator();
                Map<Long, MissionInfo> missions = new HashMap<>();
                Long missionID = Long.valueOf(1);

                //iterating on all of the missions for the specific intelligence
                while(missionsIter.hasNext()){
                    JsonObject newMissionJson = (JsonObject) missionsIter.next();
                    String name = newMissionJson.get("missionName").getAsString();

                    JsonArray serialAgentsNumbersJson = newMissionJson.get("serialAgentsNumbers").getAsJsonArray();
                    List<String> serialAgentNumbers = new ArrayList<>();
                    for(JsonElement agent : serialAgentsNumbersJson) serialAgentNumbers.add(agent.getAsString());

                    String gadget = newMissionJson.get("gadget").getAsString();
                    int duration = newMissionJson.get("duration").getAsInt();
                    int timeIssued = newMissionJson.get("timeIssued").getAsInt();
                    int timeExpired = newMissionJson.get("timeExpired").getAsInt();

                    MissionInfo newMission = new MissionInfo(
                            name, serialAgentNumbers, gadget, timeIssued, timeExpired);

                    missions.put(missionID, newMission);
                    missionID++;
                }

                threads.add(new Thread(new Intelligence(String.valueOf(i), missions)));
            }

            //Initializing Time Service
            long timeTicks = services.get("time").getAsLong();
            threads.add(new Thread(new TimeService(timeTicks)));

            for(Thread thread : threads) thread.run();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }





}
