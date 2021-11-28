package data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "Id")
public class Elevator {

    private String Id;
    private double maxWeight;
    private Size size;
    private int maxPassengerCount;
    private int speed;
    private int currentFloor;
    private final int maxFloor;
    private List<Passenger> passengers;

    public Elevator(double maxWeight, Size size, int maxPassengerCount, int speed, int maxFloor) {
        this.Id = UUID.randomUUID().toString();
        this.maxWeight = maxWeight;
        this.size = size;
        this.maxPassengerCount = maxPassengerCount;
        this.speed = speed;
        this.maxFloor = maxFloor;
        this.passengers = new ArrayList<>();
    }

    public Elevator() {
        maxFloor = 0;
    }

    public double getPassengerWeight() {
        return this
            .getPassengers()
            .stream()
            .mapToDouble(Passenger::getWeight)
            .sum();
    }

    public int getPassengerCount(){
        return this
            .getPassengers()
            .size();
    }


    @Override
    public String toString() {
        return "Elevator{" +
                "Id='" + Id + '\'' +
                ", maxWeight=" + maxWeight +
                ", size=" + size +
                ", maxPassengerCount=" + maxPassengerCount +
                ", speed=" + speed +
                ", currentFloor=" + currentFloor +
                ", maxFloor=" + maxFloor +
                ", passengers=" + passengers +
                '}';
    }
}
