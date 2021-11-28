package interactor;

import com.google.inject.Inject;
import common.ObservableHistory;
import data.Building;
import data.Elevator;
import data.Passenger;
import data.Size;
import viewModels.BuildingParameters;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PassengerManagerImpl extends ObservableHistory<PassengerManager.PassengerCallback, PassengerManager.PassengerEvent>
        implements PassengerManager{

    private final Building building;
    private final ScheduledExecutorService scheduledExecutorService;
    private final int passengerCreationDelay;
    private Future<?> scheduledTask;

    private final Runnable passengerManagerRunnable = new Runnable() {
        @Override
        public void run() {
            manageCreatingPassenger();
            scheduledTask = scheduledExecutorService.schedule(passengerManagerRunnable,
                    (int)(Math.random() * passengerCreationDelay),
                    TimeUnit.MILLISECONDS);
        }
    };

    @Inject
    public PassengerManagerImpl(BuildingParameters buildingParameters)
    {
        this.building = buildingParameters.getBuilding();
        this.passengerCreationDelay = buildingParameters.getPassengerCreationDelay();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledTask = scheduledExecutorService.schedule(passengerManagerRunnable, passengerCreationDelay, TimeUnit.MILLISECONDS);
    }

    public void manageCreatingPassenger()
    {
        var passenger = createPassenger();
        var elevator = passenger.chooseElevator(building);
        notifyOnPassengerSelectedElevator(passenger, elevator);
    }

    public Passenger createPassenger() {
        Size size = new Size((int) Math.floor(Math.random() * (100 - 40 + 1) + 40),
                (int) Math.floor(Math.random() * (220 - 20 + 1) + 20));
        double weight = Math.floor(Math.random() * (110 - 10 + 1) + 10);
        int minFloor = 0;
        int maxFloor = building.getFloorList().size();
        int currentFloor = (int) ((Math.random() * (maxFloor - minFloor)) + minFloor);
        int floorToGo;
        do {
            floorToGo = (int) ((Math.random() * (maxFloor - minFloor)) + minFloor);
        } while (floorToGo == currentFloor);
        return new Passenger(size, weight, currentFloor, floorToGo);
    }

    public void notifyOnPassengerSelectedElevator(Passenger passenger, Elevator elevator)
    {
        addHistoryItem(new PassengerEvent(passenger, elevator));
        for(PassengerCallback passengerCallback : callbackSet)
            passengerCallback.onPassengerSelectedElevator(passenger, elevator);
    }

    @Override
    public void dispose() {
        scheduledTask.cancel(true);
        scheduledExecutorService.shutdownNow();
    }
}
