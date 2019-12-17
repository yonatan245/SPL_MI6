package bgu.spl.mics.application;

import bgu.spl.mics.Event;

import java.util.List;

public class AgentsAvailableEvent<T> implements Event<T> {

    private List<String> serialAgentsNumbers;

    public AgentsAvailableEvent(List<String> serialAgentsNumbers) {
        this.serialAgentsNumbers = serialAgentsNumbers;
    }

    public List<String> getSerialAgentsNumbers() {
        return serialAgentsNumbers;
    }
}
