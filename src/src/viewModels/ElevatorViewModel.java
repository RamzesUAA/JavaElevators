package viewModels;

import com.google.inject.Inject;
import common.ObjectProperty;
import data.Elevator;
import data.Passenger;
import interactor.BuildingManager;
import interactor.ElevatorManager;
import interactor.ElevatorManagerImpl;

import java.util.List;
import java.util.Optional;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import start.Main;

@Getter
@Setter
@Log4j
public class ElevatorViewModel {

    private BuildingManager buildingManager;
    private ElevatorManager elevatorManager;
    private common.ObjectProperty<State> elevatorState = new common.ObjectProperty<>(new State());
    private Color color;

    @Inject
    public ElevatorViewModel(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
    }

    public void init(String elevatorId) {
        elevatorManager = buildingManager.getElevatorManagerMap().get(elevatorId);
        color = Main.getInjector().getInstance(BuildingParameters.class).getStrategyColorMap().get(elevatorManager.getBaseElevatorStrategy().getClass());
        if (elevatorManager instanceof ElevatorManagerImpl) {
            ((ElevatorManagerImpl) elevatorManager).subscribe((elevator, floor, acceptPassengersIds, leavingPassengers) -> {
                State tmp = new State();
                Optional<Double> currentWeightOptional = elevator.getPassengers()
                        .stream()
                        .map(Passenger::getWeight)
                        .reduce((aDouble, aDouble2) -> aDouble += aDouble2);
                tmp.currentFloor = floor;
                tmp.maxWeight = elevator.getMaxWeight();
                currentWeightOptional.ifPresent(aDouble -> tmp.currentWeight = aDouble);
                tmp.color = Color
                        .hsb(120 * (1 - tmp.currentWeight / tmp.maxWeight), 1, 0.85);
                if (tmp.currentWeight > tmp.maxWeight)
                    tmp.color = Color.BLUE;
                tmp.passengersCount = elevator.getPassengers().size();
                tmp.acceptPassengers = acceptPassengersIds;
                tmp.leavingPassengers = leavingPassengers;
                elevatorState.setValue(new State(tmp));
            });

            subscribeLogger();

        }
    }

    private void subscribeLogger() {
        ((ElevatorManagerImpl) elevatorManager).subscribe((elevator, floor, acceptPassengers, leavingPassengers) -> {
            double massLeavingPassengers = 0;
            double massAcceptPassengers = 0;
            if (leavingPassengers != null && !leavingPassengers.isEmpty())
                massLeavingPassengers = leavingPassengers.stream().map(Passenger::getWeight).reduce(Double::sum).get();
            if (acceptPassengers != null && !acceptPassengers.isEmpty())
                massAcceptPassengers = acceptPassengers.stream().map(Passenger::getWeight).reduce(Double::sum).get();

            if (leavingPassengers != null) {
                int i = leavingPassengers.size();
                for (var passenger : leavingPassengers) {
                    i--;
                    massLeavingPassengers -= passenger.getWeight();
                    if (acceptPassengers != null)
                        printLeaveLift(passenger, elevator, massLeavingPassengers, massAcceptPassengers, acceptPassengers, i);
                }
            }

            if (acceptPassengers != null) {
                int i = acceptPassengers.size();
                for (var passenger : acceptPassengers) {
                    i--;
                    massAcceptPassengers -= passenger.getWeight();
                    printEnterLift(passenger, elevator, massAcceptPassengers, i);
                }
            }
        });
    }

    private void printLeaveLift(Passenger passenger, Elevator elevator, double massLeavingPassengers,
                                double massAcceptPassengers, List<Passenger> acceptPassengers, int i) {
        log.info("LEAVE LIFT Passenger{" +
                "id=" + passenger.getId().substring(0, 7) +
                ", weight=" + passenger.getWeight() +
                ", floor=" + passenger.getCurrentFloor() +
                "} from Lift now{" +
                "id=" + elevator.getId().substring(0, 7) +
                ", passengersWeight=" +
                (elevator.getPassengerWeight() - massAcceptPassengers + massLeavingPassengers) +
                ", passengersCount=" + (elevator.getPassengerCount() + i - acceptPassengers.size()) +
                "}");
    }

    private void printEnterLift(Passenger passenger, Elevator elevator, double massAcceptPassengers, int i) {
        log.info("ENTER LIFT Passenger{" +
                "id=" + passenger.getId().substring(0, 7) +
                ", weight=" + passenger.getWeight() +
                ", floor=" + passenger.getCurrentFloor() +
                ", expectedFloor=" + passenger.getFloorToGo() +
                "} from Lift now{" +
                "id=" + elevator.getId().substring(0, 7) +
                ", passengersWeight=" + (elevator.getPassengerWeight() - massAcceptPassengers) +
                ", maxWeight=" + elevator.getMaxWeight() +
                ", passengersCount=" + (elevator.getPassengerCount() - i) +
                ", maxCount=" + elevator.getMaxPassengerCount() +
                "}");
    }

    public ObjectProperty<State> elevatorStateProperty() {
        return elevatorState;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class State {
        private int currentFloor = 0;
        private double maxWeight = 0.0;
        private double currentWeight = 0.0;
        private int passengersCount = 0;
        private List<Passenger> acceptPassengers = null;
        private List<Passenger> leavingPassengers;
        private Color color;

        public State(State state) {
            this.currentFloor = state.getCurrentFloor();
            this.maxWeight = state.getMaxWeight();
            this.currentWeight = state.getCurrentWeight();
            this.passengersCount = state.getPassengersCount();
            this.acceptPassengers = state.getAcceptPassengers();
            this.leavingPassengers = state.getLeavingPassengers();
            this.color = state.getColor();
        }
    }

}
