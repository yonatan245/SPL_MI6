//package bgu.spl.mics;
//
//import bgu.spl.mics.example.messages.ExampleBroadcast;
//import bgu.spl.mics.example.messages.ExampleEvent;
//import bgu.spl.mics.example.subscribers.ExampleBroadcastSubscriber;
//import bgu.spl.mics.example.subscribers.ExampleEventHandlerSubscriber;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class MessageBrokerTest {
//    MessageBroker mb;
//    @BeforeEach
//    public void setUp(){
//        mb=MessageBrokerImpl.getInstance();
//    }
//
//    @Test
//    void getInstance() {
//        assertNotNull(mb);
//        MessageBroker mb2 = MessageBrokerImpl.getInstance();
//        assertNotNull(mb2);
//        assertEquals(mb,mb2);
//        assertSame(mb,mb2);
//        assertSame(mb,MessageBrokerImpl.getInstance());
//    }
//
//    @Test
//    void subscribeEvent() {
//        String testname = "example";
//        String[] args = new String[10];
//        Subscriber m = new ExampleEventHandlerSubscriber(testname, args);
//        mb.register(m);
//        mb.subscribeEvent(ExampleEvent.class,m);
//        assertTrue(((ExampleEventHandlerSubscriber) m).getIsSub());
//    }
//
//    @Test
//    void subscribeBroadcast() {
//        String testname = "example";
//        String[] args = new String[10];
//        Subscriber m = new ExampleBroadcastSubscriber(testname, args);
//        mb.register(m);
//        mb.subscribeBroadcast(ExampleBroadcast.class,m);
//        assertTrue(((ExampleBroadcastSubscriber) m).getIsSub());
//    }
//
//    @Test
//    void complete() {
//        String testname = "example";
//        String sender = "me";
//        String[] args = new String[10];
//        String outcome = "yay it's done";
//        Event example= new ExampleEvent(sender);
//        Subscriber m = new ExampleEventHandlerSubscriber(testname, args);
//        mb.register(m);
//        Future<String> fut=mb.sendEvent(example);
//        mb.complete(example,outcome);
//        assertEquals(outcome,fut.get());
//    }
//
//    @Test
//    void sendBroadcast() {
//        String testname = "example";
//        String secondtest = "compare";
//        String sender = "me";
//        String[] args = new String[10];
//        Subscriber m = new ExampleBroadcastSubscriber(testname, args);
//        mb.register(m);
//        Subscriber t = new ExampleBroadcastSubscriber(secondtest, args);
//        mb.register(t);
//        mb.subscribeBroadcast(ExampleBroadcast.class,m);
//        Broadcast example=new ExampleBroadcast(sender);
//        mb.sendBroadcast(example);
//        assertEquals (((ExampleBroadcastSubscriber) m).getMessages()[0],example);
//        assertEquals (((ExampleBroadcastSubscriber) t).getMessages()[0],example);
//        assertEquals (((ExampleBroadcastSubscriber) m).getMessages()[0],((ExampleBroadcastSubscriber) t).getMessages()[0]);
//    }
//
//    @Test
//    void sendEvent() {
//        String testname = "example";
//        String sender = "me";
//        String[] args = new String[10];
//        Subscriber m = new ExampleEventHandlerSubscriber(testname, args);
//        mb.register(m);
//        mb.subscribeEvent(ExampleEvent.class,m);
//        Event example=new ExampleEvent(sender);
//        mb.sendEvent(example);
//        assertEquals (((ExampleEventHandlerSubscriber) m).getMessages()[0],example);
//    }
//
//
//    @Test
//    void register() {
//        String testname = "example";
//        String[] args = new String[10];
//        Subscriber m = new ExampleEventHandlerSubscriber(testname, args);
//        mb.register(m);
//        assertTrue(((ExampleEventHandlerSubscriber) m).getMessages().length!=0);
//    }
//
//    @Test
//    void unregister() {
//        String testname = "example";
//        String[] args = new String[10];
//        Subscriber m = new ExampleEventHandlerSubscriber(testname, args);
//        mb.register(m);
//        assertTrue(((ExampleEventHandlerSubscriber) m).getMessages().length!=0);
//        mb.unregister(m);
//        assertTrue(((ExampleEventHandlerSubscriber) m).getMessages().length==0);
//    }
//
//    @Test
//    void awaitMessage() {
//        String testname = "example";
//        String sender = "me";
//        String[] args = new String[10];
//        Subscriber m = new ExampleEventHandlerSubscriber(testname, args);
//        mb.register(m);
//        mb.subscribeEvent(ExampleEvent.class,m);
//        Event example=new ExampleEvent(sender);
//        Future<String> fut = mb.sendEvent(example);
//        String outcome = "yay it's done";
//        try{
//            mb.awaitMessage(m);
//            m.complete(example,outcome);
//            assertEquals(outcome,fut.get());
//        }
//        catch (InterruptedException e){
//            fail("Interrupted: " + e.getMessage());
//        }
//
//    }
//
//}
