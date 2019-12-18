package bgu.spl.mics;

abstract class RunnableSubPub implements Runnable {

    //Fields
    private final String name;
    private final SimplePublisher simplePublisher;

    //Constructor
    /**
     * @param name the Publisher/Subscriber name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public RunnableSubPub(String name) {
        this.name = name;
        simplePublisher = new SimplePublisher();
    }

    //Methods
    /**
     * this method is called once when the event loop starts.
     */
    protected abstract void initialize() throws InterruptedException, ClassNotFoundException;

    /**
     * @return the name of the Publisher/Subscriber - the Publisher/Subscriber name is given to it in the
     *         construction time and is used mainly for debugging purposes.
     */
    public final String getName() {
        return name;
    }

    /**
     * The entry point of the publisher/subscriber.
     * otherwise you will end up in an infinite loop.
     */
    @Override
    public abstract void run();

    /**
     * @return the simple publisher
     */
    public SimplePublisher getSimplePublisher() {
        return simplePublisher;
    }
}
