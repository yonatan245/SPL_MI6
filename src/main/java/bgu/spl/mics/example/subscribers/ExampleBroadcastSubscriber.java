package bgu.spl.mics.example.subscribers;

import bgu.spl.mics.Message;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.example.messages.ExampleBroadcast;

public class ExampleBroadcastSubscriber extends Subscriber {

    private int mbt;
    private boolean isSubscribed;
    private Message[] messages;

    public ExampleBroadcastSubscriber(String name, String[] args) {
        super(name);

        if (args.length != 1) {
            throw new IllegalArgumentException("Listener expecting a single argument: mbt (the number of requests to answer before termination)");
        }

        try {
            mbt = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Listener expecting the argument mbt to be a number > 0, instead received: " + args[0]);
        }

        if (mbt <= 0) {
            throw new IllegalArgumentException("Listener expecting the argument mbt to be a number > 0, instead received: " + args[0]);
        }
    }


    public boolean getIsSub(){
        return isSubscribed;
    }

    public Message[] getMessages(){
        return messages;
    }

    @Override
    protected void initialize() {
        System.out.println("Listener " + getName() + " started");
        
        subscribeBroadcast(ExampleBroadcast.class, message -> {
            mbt--;
            System.out.println("Listener " + getName() + " got a new message from " + message.getSenderId() + "! (mbt: " + mbt + ")");
            if (mbt == 0) {
                System.out.println("Listener " + getName() + " terminating.");
                terminate();
            }
        });
    }

}
