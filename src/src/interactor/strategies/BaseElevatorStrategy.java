package interactor.strategies;

import data.*;
import interactor.command.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseElevatorStrategy implements BaseStrategy {
    protected Map<Elevator, List<Integer>> savedCallingFloors;
    protected Map<Elevator, Integer> prevFloors;

    public abstract Command doStep (Elevator elevator, List<Passenger> waitingPassengers, List<Integer> callingFloors);

    protected List<Passenger> possiblePassengers(Elevator elevator, List<Passenger> waitingPassengers) {
        double mass = elevator.getPassengers()
                .stream()
                .mapToDouble(Passenger::getWeight)
                .reduce(0, Double::sum);

        int count = elevator.getPassengers().size();

        List<Passenger> returned = new ArrayList<>();
        for (var passenger : waitingPassengers) {
            if (acceptWeightCriteria(mass, passenger, elevator) && acceptMaxPassengerCountCriteria(count, elevator)) {
                returned.add((passenger));
                mass += passenger.getWeight();
                ++count;
            } else {
                break;
            }
        }

        return returned;
    }

    protected void removeFromMap(Elevator elevator, int floor) {
        if (savedCallingFloors.containsKey(elevator)) {
            savedCallingFloors.get(elevator).remove(Integer.valueOf(floor));
        }
    }

    protected void updateMap(Elevator elevator, List<Integer> callingFloors) {
        if (!savedCallingFloors.containsKey(elevator)) {
            savedCallingFloors.put(elevator, callingFloors.stream().distinct().collect(Collectors.toList()));
        } else {
            var list = savedCallingFloors.getOrDefault(elevator, new ArrayList<>());
            list = Stream.concat(list.stream(), callingFloors.stream()).distinct().collect(Collectors.toList());
            savedCallingFloors.put(elevator, list);
        }
    }

    private boolean acceptWeightCriteria(double currMass, Passenger passenger, Elevator elevator)
    {
        return currMass + passenger.getWeight() < elevator.getMaxWeight();
    }

    private boolean acceptMaxPassengerCountCriteria(double count, Elevator elevator)
    {
        return count < elevator.getMaxPassengerCount();
    }

}
