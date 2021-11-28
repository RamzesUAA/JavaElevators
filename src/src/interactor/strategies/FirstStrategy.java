package interactor.strategies;

import data.Elevator;
import data.Passenger;
import interactor.command.Command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FirstStrategy extends BaseElevatorStrategy {
    @Override
    public Command doStep(Elevator elevator, List<Passenger> waitingPassengers, List<Integer> callingFloors) {
        int floorToGo = elevator.getCurrentFloor();
        List<Passenger> leavingPassengers=new ArrayList<>();
        List<Passenger> acceptingPassengers=new ArrayList<>();

        List<Integer> callings=getDistinctCallingFloors(callingFloors);

        if(elevator.getPassengers().isEmpty())
        {
            floorToGo=getNearestCalling(elevator,callings);
            if(floorToGo==elevator.getCurrentFloor())
            {
               acceptingPassengers=getFittablePassengers(waitingPassengers,elevator);
            }
        }else {
            List<Integer> passengerFloors=getPassengerFloors(elevator);
            if(needMoveUp(passengerFloors,elevator)&&needMoveDown(passengerFloors,elevator))
            {
                floorToGo=getShortestPath(passengerFloors,elevator);
            }else{
                floorToGo=getNearestCalling(elevator,passengerFloors);
            }
            leavingPassengers=getLeavingPassengers(elevator,floorToGo);
        }
        return new Command(floorToGo,leavingPassengers,acceptingPassengers);
    }
    private boolean needMoveUp(List<Integer> passengerFloors,Elevator elevator)
    {
        int currentFloor=elevator.getCurrentFloor();
        return passengerFloors.stream().anyMatch(i -> i > currentFloor);
    }
    private boolean needMoveDown(List<Integer> passengerFloors,Elevator elevator)
    {
        int currentFloor=elevator.getCurrentFloor();
        return passengerFloors.stream().anyMatch(i -> i <= currentFloor);
    }
    private int getShortestPath(List<Integer> passengerFloors,Elevator elevator)
    {
        int shortestPathFloorNumber;
        int maxCalling=passengerFloors.stream().max(Comparator.comparingInt(i->i)).orElse(elevator.getMaxFloor());
        int minCalling=passengerFloors.stream().min(Comparator.comparingInt(i->i)).orElse(0);
        maxCalling=Math.abs(maxCalling-elevator.getCurrentFloor());
        minCalling=Math.abs(minCalling-elevator.getCurrentFloor());
        if(maxCalling<minCalling)
        {
            List<Integer> passengersFloorsAbove= getPassengersFloorsAbove(passengerFloors, elevator);
            shortestPathFloorNumber = getNearestCalling(elevator,passengersFloorsAbove);
        }else
        {
            List<Integer> passengerFloorsBelow = getPassengersFloorsBelow(passengerFloors,elevator);
            shortestPathFloorNumber = getNearestCalling(elevator,passengerFloorsBelow);
        }
        return shortestPathFloorNumber;
    }

    private List<Integer> getPassengersFloorsAbove(List<Integer> passengerFloors, Elevator elevator)
    {
        int currentFloor=elevator.getCurrentFloor();
        return passengerFloors
                .stream()
                .filter(i->i>currentFloor)
                .collect(Collectors
                        .toList());
    }

    private List<Integer> getPassengersFloorsBelow(List<Integer> passengerFloors, Elevator elevator)
    {
        int currentFloor=elevator.getCurrentFloor();
        return passengerFloors
                .stream()
                .filter(i->i<=currentFloor)
                .collect(Collectors
                        .toList());
    }

    private int getNearestCalling(Elevator elevator,List<Integer> distinctCalling)
    {
        int currentFloor = elevator.getCurrentFloor();
        return distinctCalling
                .stream()
                .min(Comparator
                        .comparingInt(p->Math
                                .abs(p-currentFloor))).orElse(currentFloor);

    }

    private List<Integer> getPassengerFloors(Elevator elevator)
    {
        return elevator
                .getPassengers()
                .stream()
                .map(Passenger::getFloorToGo)
                .distinct()
                .collect(Collectors
                        .toList());
    }
    private List<Integer> getDistinctCallingFloors(List<Integer> callingFloors)
    {
        if(callingFloors!=null) {
            return callingFloors
                    .stream()
                    .distinct()
                    .collect(Collectors
                            .toList());
        }
        else {
            return new ArrayList<>();
        }
    }

    private List<Passenger> getFittablePassengers(List<Passenger> waitingPassengers,Elevator elevator)
    {
        List<Passenger> fittablePassengers=new ArrayList<>();

        double passengerWeight = elevator
                .getPassengers()
                .stream()
                .mapToDouble(Passenger::getWeight)
                .sum();

        int passengerCount=elevator
                .getPassengers()
                .size();

        if(waitingPassengers!=null) {
            for (Passenger waitingPassenger : waitingPassengers) {
                if ((elevator.getMaxWeight() - passengerWeight) >= waitingPassenger.getWeight()
                        && passengerCount < elevator.getMaxPassengerCount()) {
                    fittablePassengers.add(waitingPassenger);
                    passengerCount++;
                    passengerWeight += waitingPassenger.getWeight();
                }
            }
        }

        return fittablePassengers;
    }

    private List<Passenger> getLeavingPassengers(Elevator elevator,int floor)
    {
        return elevator
                .getPassengers()
                .stream()
                .filter(p->p
                        .getFloorToGo()==floor)
                .collect(Collectors
                        .toList());
    }
}
