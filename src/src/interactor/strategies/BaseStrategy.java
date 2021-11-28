package interactor.strategies;

import data.Elevator;
import data.Passenger;
import interactor.command.Command;

import java.util.List;

public interface BaseStrategy {
    Command doStep (Elevator elevator, List<Passenger> waitingPassengers, List<Integer> callingFloors);
}
