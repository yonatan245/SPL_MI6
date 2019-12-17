package bgu.spl.mics;

public class TopicNode {

    //Fields
    private Subscriber subscriber;
    private TopicNode next;
    private TopicNode prev;

    //Constructor
    public TopicNode(Subscriber subscriber){
        this.subscriber = subscriber;
    }

    //Methods
    public Subscriber getSubscriber() {
        return subscriber;
    }

    public TopicNode getNext() {
        return next;
    }

    public void setNext(TopicNode next) {
        this.next = next;
    }

    public TopicNode getPrev() {
        return prev;
    }

    public void setPrev(TopicNode prev) {
        this.prev = prev;
    }

    public boolean isAlone() {return next == this;}

    public void remove(){
        if(!isAlone()){
            next.setPrev(prev);
            prev.setNext(next);
        }
    }

    public void insert(TopicNode prev, TopicNode next){
        this.next = next;
        this.prev = prev;

        next.prev = this;
        prev.next = this;
    }
}
