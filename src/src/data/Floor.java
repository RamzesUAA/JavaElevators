package data;

import java.util.*;
import java.util.function.Consumer;

public class Floor {
    private int number;
    private Map<Elevator, Queue<Passenger>> passengerQueues = new HashMap<>();


    public Floor(int number) {
        this.number = number;
    }

    public Floor(int number, List<Elevator> elevators) {
        this.number = number;
        elevators.forEach(this::addElevator);
    }

    public void addElevator(Elevator elevator)
    {
        passengerQueues.put(elevator, new ArrayDeque<>());
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Map<Elevator, Queue<Passenger>> getPassengerQueues() {
        return passengerQueues;
    }

}
