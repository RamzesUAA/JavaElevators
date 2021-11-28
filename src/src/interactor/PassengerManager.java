package interactor;

import common.CallbackAble;
import common.Disposable;
import data.Elevator;
import data.Passenger;

public interface PassengerManager extends Disposable {

    void manageCreatingPassenger();

    Passenger createPassenger();

    interface PassengerCallback extends CallbackAble<PassengerManagerImpl.PassengerEvent> {
        void onPassengerSelectedElevator(Passenger passenger, Elevator elevator);
    }

    class PassengerEvent
    {
        private Passenger passenger;
        private Elevator elevator;

        public PassengerEvent(Passenger passenger, Elevator elevator) {
            this.passenger = passenger;
            this.elevator = elevator;
        }

        public Passenger getPassenger() {
            return passenger;
        }

        public void setPassenger(Passenger passenger) {
            this.passenger = passenger;
        }

        public Elevator getElevator() {
            return elevator;
        }

        public void setElevator(Elevator elevator) {
            this.elevator = elevator;
        }
    }
}
