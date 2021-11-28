package interactor;

import common.Observable;
import data.Building;
import data.Elevator;
import data.Passenger;
import interactor.command.Command;
import interactor.strategies.BaseElevatorStrategy;
import interactor.strategies.FirstStrategy;
import interactor.strategies.SecondStrategy;
import viewModels.BuildingParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElevatorManagerImpl extends Observable<ElevatorManager.ElevatorCallback> implements ElevatorManager {

    private BaseElevatorStrategy baseElevatorStrategy;
    private final Elevator elevator;
    private final ScheduledExecutorService scheduledExecutorService;
    private Command currentElevatorCommand;
    private final Building building;
    private final Future<?> scheduledTask;

    @Override
    public BaseElevatorStrategy getBaseElevatorStrategy() {
        return baseElevatorStrategy;
    }

    public ElevatorManagerImpl(Elevator elevator, BuildingParameters buildingParameters) {
        this.elevator = elevator;
        this.building = buildingParameters.getBuilding();
        long delay = 1500 / buildingParameters.getSpeed().getSpeed();
        currentElevatorCommand = new Command(0, new ArrayList<>(), new ArrayList<>());
        switch (buildingParameters.getStrategyNum()) {
            case 1:
                this.baseElevatorStrategy = new FirstStrategy();
                break;
            case 2:
                this.baseElevatorStrategy = new SecondStrategy();
                break;
            case 3:
                this.baseElevatorStrategy = Math.random() > 0.5 ? new FirstStrategy() : new SecondStrategy();
                break;
        }
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        Runnable elevatorCommand = () -> {
            try {
                currentElevatorCommand = baseElevatorStrategy.doStep(elevator,
                        getWaitingPassengersList(currentElevatorCommand.getFloorToGo()),
                        getCallingByFloorList());
                if (elevator.getCurrentFloor() < currentElevatorCommand.getFloorToGo()) {
                    moveUp();
                } else if (elevator.getCurrentFloor() > currentElevatorCommand.getFloorToGo()) {
                    moveDown();
                } else {
                    stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        scheduledTask = scheduledExecutorService.scheduleAtFixedRate(elevatorCommand, delay, delay, TimeUnit.MILLISECONDS);
    }

    private void moveUp() {
        elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
        notifyOnElevatorPropertyChanged(elevator, elevator.getCurrentFloor(), null,null);
    }

    private void moveDown() {
        elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
        notifyOnElevatorPropertyChanged(elevator, elevator.getCurrentFloor(), null,null);
    }

    private void stop() {
        elevator.getPassengers().removeAll(currentElevatorCommand.getLeavingPassengers());
        elevator.getPassengers().addAll(currentElevatorCommand.getAcceptingPassengers());
        getWaitingPassengersQueue(elevator.getCurrentFloor()).removeAll(currentElevatorCommand.getAcceptingPassengers());
        List<Passenger> acceptPassengersIds = currentElevatorCommand.getAcceptingPassengers();
        notifyOnElevatorPropertyChanged(elevator, elevator.getCurrentFloor(), acceptPassengersIds,currentElevatorCommand.getLeavingPassengers());
    }

    private Queue<Passenger> getWaitingPassengersQueue(int floorNumber) {
        return building
                .getFloorList()
                .stream()
                .filter(f -> f.getNumber() == floorNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("There isn't such floor, floor = " + floorNumber))
                .getPassengerQueues()
                .get(elevator);
    }

    private List<Passenger> getWaitingPassengersList(int floorNumber) {
        return new ArrayList<>(getWaitingPassengersQueue(floorNumber));
    }

    private List<Integer> getCallingByFloorList() {
        return building
                .getFloorList()
                .stream()
                .filter(f -> !f
                        .getPassengerQueues()
                        .get(elevator)
                        .isEmpty())
                .flatMap(f -> Stream
                        .of(f.getNumber()))
                .collect(Collectors
                        .toList());
    }

    private void notifyOnElevatorPropertyChanged(Elevator elevator, int floor, List<Passenger> acceptPassengers, List<Passenger> leavingPassengers)
    {
        for(ElevatorCallback elevatorCallback : callbackSet)
            elevatorCallback.onElevatorPropertyChanged(elevator, floor, acceptPassengers,leavingPassengers);
    }

    @Override
    public Elevator getElevator() {
        return elevator;
    }


    @Override
    public void dispose() {
        scheduledTask.cancel(true);
        scheduledExecutorService.shutdownNow();
    }
}
