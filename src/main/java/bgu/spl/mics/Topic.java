package bgu.spl.mics;

import java.util.LinkedList;

public class Topic {

    //Fields
    private TopicNode firstSubscriber;
    private TopicNode lastSubscriber;
    private TopicNode currentSubscriber;

    //Constructor
    public Topic(){
        firstSubscriber = null;
        lastSubscriber = null;
        currentSubscriber = null;
    }

    //Methods
    public void add(SimplePublisher toAdd){
        TopicNode newNode = new TopicNode(toAdd);

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

    public void remove(SimplePublisher toRemove){
        if(!isEmpty()){
            TopicNode nodeToRemove = null;
            TopicNode toCheck = firstSubscriber;

            do{
                if(toCheck.getPublisher().equals(toRemove)) nodeToRemove = toCheck;
            } while(nodeToRemove != null && toCheck.getNext() != firstSubscriber);


            if(nodeToRemove != null){
                if(nodeToRemove.isAlone()){
                    firstSubscriber = null;
                    lastSubscriber = null;
                    currentSubscriber = null;
                }
                if(firstSubscriber.equals(nodeToRemove)) firstSubscriber = nodeToRemove.getNext();
                if(currentSubscriber.equals(nodeToRemove)) currentSubscriber = nodeToRemove.getNext();
                if(lastSubscriber.equals(nodeToRemove)) lastSubscriber = nodeToRemove.getPrev();

                nodeToRemove.remove();
            }
        }
    }

    public boolean isEmpty(){return firstSubscriber == null;}



}
