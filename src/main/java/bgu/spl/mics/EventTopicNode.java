package bgu.spl.mics;

public class EventTopicNode {

    //Fields
    private Subscriber subscriber;
    private EventTopicNode next;
    private EventTopicNode prev;

    //Constructor
    public EventTopicNode(Subscriber subscriber){
        this.subscriber = subscriber;
    }

    //Methods
    public Subscriber getSubscriber() {
        return subscriber;
    }

    public EventTopicNode getNext() {
        return next;
    }

    public void setNext(EventTopicNode next) {
        this.next = next;
    }

    public EventTopicNode getPrev() {
        return prev;
    }

    public void setPrev(EventTopicNode prev) {
        this.prev = prev;
    }

    public boolean isAlone() {return next == this;}

    public void remove(){
        if(!isAlone()){
            next.setPrev(prev);
            prev.setNext(next);
        }
    }

    public void insert(EventTopicNode prev, EventTopicNode next){
        this.next = next;
        this.prev = prev;

        next.prev = this;
        prev.next = this;
    }
}
