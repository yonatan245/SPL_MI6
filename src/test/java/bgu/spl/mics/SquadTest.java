package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {

    Squad squad;
    Agent[] agents;

    @BeforeEach
    public void setUp(){
        squad = Squad.getInstance();
        Agent yonatan = new Agent();
        Agent omer = new Agent();
        Agent ziv = new Agent();
        Agent idan = new Agent();

        yonatan.setName("Yonatan");
        omer.setName("Omer");
        ziv.setName("Ziv");
        idan.setName("Idan");

        yonatan.setSerialNumber("001");
        omer.setSerialNumber("002");
        ziv.setSerialNumber("004");
        idan.setSerialNumber("0010");

        agents = new Agent[]{yonatan, omer, ziv, idan};
        squad.load(agents);
    }

    @Test
    public void testGetAgents(){
        List<String> goodSerial = new ArrayList<String>();
        goodSerial.add("001");
        goodSerial.add("002");
        goodSerial.add("004");
        goodSerial.add("0010");

        List<String> goodSerialNotAll = new ArrayList<String>();
        goodSerialNotAll.add("001");
        goodSerialNotAll.add("002");
        goodSerialNotAll.add("0010");

        List<String> badSerialWithUnexisted = new ArrayList<String>();
        badSerialWithUnexisted.add("001");
        badSerialWithUnexisted.add("002");
        badSerialWithUnexisted.add("004");
        badSerialWithUnexisted.add("0010");
        badSerialWithUnexisted.add("005");

        assertTrue(squad.getAgents(goodSerial), "Doesn't find all the agents");
        assertTrue(squad.getAgents(goodSerialNotAll), "Doesn't find part of the agents");
        assertFalse(squad.getAgents(badSerialWithUnexisted), "Finds ");


    }
}
