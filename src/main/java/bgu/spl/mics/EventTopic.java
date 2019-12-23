package bgu.spl.mics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EventTopic {

    //Fields
    private EventTopicNode firstSubscriber;
    private EventTopicNode lastSubscriber;
    private EventTopicNode currentSubscriber;

    //Constructor
    public EventTopic(){
        firstSubscriber = null;
        lastSubscriber = null;
        currentSubscriber = null;
    }

    //Methods
    public void add(Subscriber toAdd){
        EventTopicNode newNode = new EventTopicNode(toAdd);

        if(isEmpty()){
            firstSubscriber = newNode;
            lastSubscriber = newNode;
            currentSubscriber = newNode;

            newNode.setNext(newNode);
            newNode.setPrev(newNode);
        }
        else{
            newNode.insert(lastSubscriber, firstSubscriber);
            lastSubscriber = newNode;
        }
    }

    public void remove(Subscriber toRemove){
        if(!isEmpty()){
            EventTopicNode nodeToRemove = null;
            EventTopicNode toCheck = firstSubscriber;

            do{
                if(toCheck.getSubscriber().equals(toRemove)) nodeToRemove = toCheck;
            } while(nodeToRemove != null && toCheck.getNext() != firstSubscriber);


            if(nodeToRemove != null){
                if(nodeToRemove.isAlone()){
                    firstSubscriber = null;
                    lastSubscriber = null;
                    currentSubscriber = null;
                }
                else {
                    if (firstSubscriber.equals(nodeToRemove)) firstSubscriber = nodeToRemove.getNext();
                    if (currentSubscriber.equals(nodeToRemove)) currentSubscriber = nodeToRemove.getNext();
                    if (lastSubscriber.equals(nodeToRemove)) lastSubscriber = nodeToRemove.getPrev();
                }
                nodeToRemove.remove();
            }
        }
    }

    public boolean isEmpty(){return firstSubscriber == null;}

    public Subscriber getNextSubscriber() {

        Subscriber toReturn = currentSubscriber.getSubscriber();
        currentSubscriber = currentSubscriber.getNext();

        return toReturn;
    }


}
