package interactor;

import common.Disposable;
import data.Elevator;
import data.Passenger;
import interactor.strategies.BaseElevatorStrategy;
import java.util.List;

public interface ElevatorManager extends Disposable {

    Elevator getElevator();

    BaseElevatorStrategy getBaseElevatorStrategy();

    interface ElevatorCallback {
        void onElevatorPropertyChanged(Elevator elevator, int floor, List<Passenger> acceptPassengersIds, List<Passenger> leavingPassengersIds);
    }
}
