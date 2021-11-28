package interactor.strategies;

import data.Elevator;
import data.Passenger;
import interactor.command.Command;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SecondStrategy extends BaseElevatorStrategy {

    public SecondStrategy() {
        savedCallingFloors = new HashMap<>();
        prevFloors = new HashMap<>();
    }

    @Override
    public Command doStep(Elevator elevator, List<Passenger> waitingPassengers, List<Integer> callingFloors) {
        int currentFloor = elevator.getCurrentFloor();
        updateMap(elevator, callingFloors);

        List<Passenger> leavingPassengers = getLeavingPassengers(elevator, currentFloor);
        List<Passenger> stayingPassengers = getStayingPassengers(elevator, currentFloor);
        var possiblePassengers = getPossiblePassengers(elevator, stayingPassengers, waitingPassengers);
        if (!possiblePassengers.isEmpty() || !leavingPassengers.isEmpty()) {
            return new Command(currentFloor, leavingPassengers, possiblePassengers);
        }

        int prevFloor = prevFloors.getOrDefault(elevator, currentFloor);
        Map<Integer, Integer> floorPriorities = new HashMap<>();

        int nextFloor;
        OptionalInt nextFloorCF;

        if (currentFloor < prevFloor) {

            if (!stayingPassengers.isEmpty()) {

                floorPriorities.putAll(getFloorPrioritiesMap(stayingPassengers, currentFloor, 1,
                        floor -> floor < currentFloor));
                floorPriorities.putAll(getFloorPrioritiesMap(stayingPassengers, currentFloor, 4,
                        floor -> floor >= currentFloor));
                nextFloor = StrategyUtility.getKeyOfMinimumValue(floorPriorities);

            } else {

                nextFloorCF = callingFloors
                        .stream()
                        .filter(floor -> floor < currentFloor)
                        .mapToInt(floor -> floor)
                        .max();
                nextFloor = nextFloorCF.orElse(currentFloor);

            }
            
        } else {

            if (!stayingPassengers.isEmpty()) {

                floorPriorities.putAll(getFloorPrioritiesMap(stayingPassengers, currentFloor, 1,
                        floor -> floor > currentFloor));
                floorPriorities.putAll(getFloorPrioritiesMap(stayingPassengers, currentFloor, 4,
                        floor -> floor <= currentFloor));
                nextFloor = StrategyUtility.getKeyOfMinimumValue(floorPriorities);

            } else {
                Integer lowestDifferenceFloor = null;
                var lowestDifference = Integer.MAX_VALUE;
                for (int i=0; i < callingFloors.size();i++){
                    var nowDifference = currentFloor - callingFloors.get(i);
                    if(lowestDifference >= nowDifference){
                        lowestDifference = nowDifference;
                        lowestDifferenceFloor = callingFloors.get(i);
                    }
                }
                nextFloor = lowestDifferenceFloor != null ? lowestDifferenceFloor : currentFloor;
            }

        }

        removeFromMap(elevator, nextFloor);
        return new Command(currentFloor + (int) Math.signum(nextFloor - currentFloor),
                leavingPassengers, possiblePassengers);
    }

    List<Passenger> getLeavingPassengers(Elevator elevator, int currentFloor) {
        return elevator
            .getPassengers()
            .stream()
            .filter(passenger -> passenger.getFloorToGo() == currentFloor)
            .collect(Collectors.toList());
    }

    List<Passenger> getStayingPassengers(Elevator elevator, int currentFloor) {
        return elevator
                .getPassengers()
                .stream()
                .filter(passenger -> passenger.getFloorToGo() != currentFloor)
                .collect(Collectors.toList());
    }

    List<Passenger> getPossiblePassengers(Elevator elevator, List<Passenger> staying, List<Passenger> waiting) {
        Elevator tempEl = new Elevator();
        tempEl.setMaxPassengerCount(elevator.getMaxPassengerCount());
        tempEl.setMaxWeight(elevator.getMaxWeight());
        tempEl.setPassengers(staying);
        return possiblePassengers(tempEl, waiting);
    }

    Map<Integer, Integer> getFloorPrioritiesMap(List<Passenger> passengers, int currentFloor,
                                                int multiplier, Predicate<Integer> filter) {
        return passengers
                .stream()
                .mapToInt(Passenger::getFloorToGo)
                .boxed()
                .filter(filter)
                .collect(Collectors.toMap(floor -> floor,
                        floor -> multiplier * Math.abs(floor - currentFloor),
                        (first, second) -> first));
    }
}
