package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    private Future<Agent[]> futureEvent;
    @BeforeEach
    public void setUp(){
        this.futureEvent=createFutureEvent();
    }

    protected Future<Agent[]> createFutureEvent() {
        return new Future<Agent[]>();
    }


    @Test
    public void TestGet() {
        Agent[] result=new Agent[1];
        this.futureEvent.resolve(result);
        try {
            this.futureEvent.get();
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testGetException() {
        try {
            this.futureEvent.get();
            fail("Exception expected!");
        } catch (Exception e) {
            // test pass
        }
    }

    @Test
    public void TestResolve() {
        Agent[] result=new Agent[1];
        try {
            this.futureEvent.resolve(result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testResolveException() {
        try {
            this.futureEvent.resolve(null);
            fail("Exception expected!");
        } catch (Exception e) {
            // test pass
        }
    }

    @Test
    public void TestisDone() {
        Agent[] result=new Agent[1];
        assertEquals(false, this.futureEvent.isDone());
        this.futureEvent.resolve(result);
        assertEquals(true, this.futureEvent.isDone());
    }

    @Test
    public void testGetTime() {

    }
    public void test(){
        this.testGetException();
        this.TestGet();
        this.TestisDone();
        this.TestResolve();
        this.testGetTime();

        fail("Not a good test");

    }
}
