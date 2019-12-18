package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.application.subscribers.M;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Iterator;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {

        String filePath = "src/input201 - 2.json";
        Gson gson = new Gson();

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




        } catch (IOException e) {
            e.printStackTrace();
        }


    }





}
