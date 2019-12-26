package bgu.spl.mics.application;
import bgu.spl.mics.Names;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {

        List<Thread> intelligences = new ArrayList<>();
        List<Thread> threadList = new ArrayList<>();

        try {
            initialize(args[0], threadList, intelligences);

            for(Thread thread : threadList) thread.start();
            for(Thread thread : intelligences) thread.start();

            Thread timeService = new Thread(new TimeService(getTimeTicks(args[0])));
            timeService.start();

            timeService.join();

            for(Thread thread : intelligences) if(thread.isAlive()) thread.interrupt();
            for(Thread thread : intelligences) thread.join();
            for(Thread thread : threadList) if(thread.isAlive()) thread.interrupt();
            for(Thread thread : threadList) thread.join();

        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

        Inventory.getInstance().printToFile(args[1]);
        Diary.getInstance().printToFile(args[2]);
    }

    static private void initialize(String filePath, List<Thread> threadList, List<Thread> intelligences) throws FileNotFoundException {

        JsonReader reader = new JsonReader(new FileReader(filePath));
        JsonElement e = JsonParser.parseReader(reader);
        int timeTicks = getTimeTicks(filePath);

        //Initialize Inventory
        JsonArray inventoryJson = e.getAsJsonObject().get("inventory").getAsJsonArray();
        initInventory(inventoryJson);

        //Initialize Squad
        JsonArray squadJson = e.getAsJsonObject().get("squad").getAsJsonArray();
        initSquad(squadJson);

        //Initialize Services
        JsonObject services = e.getAsJsonObject().get("services").getAsJsonObject();

        //Initialize Q
        threadList.add(new Thread(new Q(timeTicks)));

        //Initialize M
        int numberOfMs = services.get("M").getAsInt();
        initM(numberOfMs, threadList, timeTicks);

        //Initialize Money Penny
        int numberOfMoneyPennies = services.get("Moneypenny").getAsInt();
        initMoneyPenny(numberOfMoneyPennies, threadList, timeTicks);

        //Initialize Intelligence
        JsonArray intelligenceJson = services.get("intelligence").getAsJsonArray();
        initIntelligence(intelligenceJson, intelligences, timeTicks);

    }

    static private void initInventory(JsonArray inventoryJson){
        Inventory inventory = Inventory.getInstance();
        String[] gadgetsToLoad = new String[inventoryJson.size()];
        int i = 0;

        for(JsonElement gadgetJson : inventoryJson){
            gadgetsToLoad[i] = gadgetJson.getAsString();
            i++;
        }
        inventory.load(gadgetsToLoad);
    }

    static private void initSquad(JsonArray squadJson){

        Gson gson = new Gson();
        Squad squad = Squad.getInstance();
        Agent[] agentsToLoad = new Agent[squadJson.size()];
        int i = 0;

        for(JsonElement agentJson : squadJson){
//            Agent toAdd = gson.fromJson(agentJson, Agent.class);
            String serialNumber = agentJson.getAsJsonObject().get("serialNumber").getAsString();
            String name = agentJson.getAsJsonObject().get("name").getAsString();
            Agent toAdd = new Agent(serialNumber, name);
            agentsToLoad[i] = toAdd;
            i++;
        }

        squad.load(agentsToLoad);
    }

    static private void initM(int numberOfMs, List<Thread> threadList, int timeTicks){
        for(int i = 0 ; i <= numberOfMs ; i++){
            threadList.add(new Thread(new M(i, timeTicks)));
        }
    }

    static private void initMoneyPenny(int numberOfMoneyPennies, List<Thread> threadList, int timeTicks){
        for(int i = 0 ; i <= numberOfMoneyPennies ; i++) threadList.add(new Thread(new Moneypenny(i+1, timeTicks)));
    }

    static private void initIntelligence(JsonArray intelligenceJson, List<Thread> threadList, int timeTicks){
        Iterator<JsonElement> intelligenceIter = intelligenceJson.iterator();
        int i = 0;

        //iterating on all of the intelligences
        while(intelligenceIter.hasNext()){
            JsonArray missionsJson = intelligenceIter.next().getAsJsonObject().get("missions").getAsJsonArray();
            Iterator<JsonElement> missionsIter = missionsJson.iterator();
            Map<Integer, List<MissionInfo>> missions = new HashMap<>();

            //iterating on all of the missions for the specific intelligence
            while(missionsIter.hasNext()){
                JsonObject newMissionJson = (JsonObject) missionsIter.next();
                String name = newMissionJson.get("name").getAsString();

                JsonArray serialAgentsNumbersJson = newMissionJson.get("serialAgentsNumbers").getAsJsonArray();
                List<String> serialAgentNumbers = new ArrayList<>();
                for(JsonElement agent : serialAgentsNumbersJson) serialAgentNumbers.add(agent.getAsString());

                String gadget = newMissionJson.get("gadget").getAsString();
                int duration = newMissionJson.get("duration").getAsInt();
                int timeIssued = newMissionJson.get("timeIssued").getAsInt();
                int timeExpired = newMissionJson.get("timeExpired").getAsInt();

                MissionInfo newMission = new MissionInfo(
                        name, serialAgentNumbers, gadget, timeIssued, timeExpired, duration);

                if(missions.get(timeIssued) == null) missions.put(timeIssued, new ArrayList<>());
                missions.get(timeIssued).add(newMission);
            }

            threadList.add(new Thread(new Intelligence(String.valueOf(i), missions, timeTicks)));
        }
    }

    static private int getTimeTicks(String filePath) throws FileNotFoundException {

        JsonReader reader = new JsonReader(new FileReader(filePath));
        JsonElement e = JsonParser.parseReader(reader);

        return e.getAsJsonObject().get("services").getAsJsonObject().get("time").getAsInt();
    }

}

