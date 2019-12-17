//package bgu.spl.mics;
//
//import bgu.spl.mics.application.passiveObjects.Agent;
//import bgu.spl.mics.application.passiveObjects.Squad;
//import org.graalvm.compiler.hotspot.nodes.CurrentJavaThreadNode;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class SquadTest {
//
//    Squad squad;
//    Agent[] agents;
//
//    @BeforeEach
//    public void setUp(){
//        squad = Squad.getInstance();
//        Agent yonatan = new Agent();
//        Agent omer = new Agent();
//        Agent ziv = new Agent();
//        Agent idan = new Agent();
//
//        yonatan.setName("Yonatan");
//        omer.setName("Omer");
//        ziv.setName("Ziv");
//        idan.setName("Idan");
//
//        yonatan.setSerialNumber("001");
//        omer.setSerialNumber("002");
//        ziv.setSerialNumber("004");
//        idan.setSerialNumber("0010");
//
//        agents = new Agent[]{yonatan, omer, ziv, idan};
//        squad.load(agents);
//    }
//
//    @Test
//    public void testGetAgents(){
//        List<String> goodSerial = new ArrayList<String>();
//        goodSerial.add("001");
//        goodSerial.add("002");
//        goodSerial.add("004");
//        goodSerial.add("0010");
//
//        List<String> goodSerialNotAll = new ArrayList<String>();
//        goodSerialNotAll.add("001");
//        goodSerialNotAll.add("002");
//        goodSerialNotAll.add("0010");
//
//        List<String> badSerialWithUnexisted = new ArrayList<String>();
//        badSerialWithUnexisted.add("001");
//        badSerialWithUnexisted.add("002");
//        badSerialWithUnexisted.add("004");
//        badSerialWithUnexisted.add("0010");
//        badSerialWithUnexisted.add("005");
//
//        List<String> badSerialOnlyUnexisted = new ArrayList<String>();
//        badSerialWithUnexisted.add("007");
//        badSerialWithUnexisted.add("003");
//        badSerialWithUnexisted.add("009");
//        badSerialWithUnexisted.add("0011");
//        badSerialWithUnexisted.add("005");
//
//        assertTrue(squad.getAgents(goodSerial), "Doesn't find all the agents");
//        assertTrue(squad.getAgents(goodSerialNotAll), "Doesn't find part of the agents");
//        assertFalse(squad.getAgents(badSerialWithUnexisted), "Finds unexisted agents along with existed");
//        assertFalse(squad.getAgents(badSerialOnlyUnexisted), "Finds unexisted agents");
//    }
//
//    @Test
//    public void testReleaseAgents(){
//        List<String> toReleaseSerial = new ArrayList<String>();
//        toReleaseSerial.add("0010");
//        toReleaseSerial.add("004");
//
//        List<String> remainedSerial = new ArrayList<String>();
//        remainedSerial.add("001");
//        remainedSerial.add("002");
//
//        squad.releaseAgents(toReleaseSerial);
//
//        assertTrue(squad.getAgents(remainedSerial), "Releases irrelevant agents");
//        assertFalse(squad.getAgents(remainedSerial), "Releases irrelevant agents");
//    }
//
//    @Test
//    public void testGetAgentNames(){
//        List<String> serialAll = new ArrayList<String>();
//        serialAll.add("001");
//        serialAll.add("002");
//        serialAll.add("004");
//        serialAll.add("0010");
//
//        List<String> expectedAll = new ArrayList<String>();
//        expectedAll.add("Yonatan");
//        expectedAll.add("Omer");
//        expectedAll.add("Ziv");
//        expectedAll.add("Idan");
//
//        List<String> serialPart = new ArrayList<String>();
//        serialAll.add("001");
//        serialAll.add("0010");
//
//        List<String> expectedPart = new ArrayList<String>();
//        expectedAll.add("Yonatan");
//        expectedAll.add("Idan");
//
//        assertLinesMatch(squad.getAgentsNames(serialAll), expectedAll);
//        assertLinesMatch(squad.getAgentsNames(serialPart), expectedPart);
//    }
//
//    @Test
//    public void testSendAgents(){
//        List<String> toSend = new ArrayList<String>();
//        toSend.add("001");
//        toSend.add("002");
//        toSend.add("0010");
//
//        List<String> toKeep = new ArrayList<String>();
//        toKeep.add("004");
//
//        squad.sendAgents(toSend, 50);
//
//        assertFalse(squad.getAgents(toSend));
//        assertTrue(squad.getAgents(toKeep));
//
//        try {
//            Thread.sleep(100);
//        } catch (Exception ignored) {}
//
//        assertTrue(squad.getAgents(toSend));
//        assertTrue(squad.getAgents(toKeep));
//    }
//}
