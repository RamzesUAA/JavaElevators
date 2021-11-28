package viewModels;

import com.google.inject.Inject;
import data.Elevator;
import data.Passenger;
import interactor.PassengerManager;
import interactor.PassengerManagerImpl;
import lombok.extern.log4j.Log4j;

@Log4j
public class PassengerViewModel {
    private int floorNumber = 0;
    private PassengerCallback passengerCallback;
    private PassengerManager passengerManager;
    private String elevatorId = null;

    @Inject
    public PassengerViewModel(PassengerManager passengerManager)
    {
        this.passengerManager = passengerManager;
    }

    public void init(String elevatorId, int floorNumber) {
        this.floorNumber = floorNumber;
        this.elevatorId = elevatorId;
        if(passengerManager instanceof PassengerManagerImpl)
        {
            ((PassengerManagerImpl)passengerManager).subscribe(new PassengerManager.PassengerCallback() {
                @Override
                public void onPassengerSelectedElevator(Passenger passenger, Elevator elevator) {
                    if(elevator.getId().equals(elevatorId)) {


                        if (passenger.getCurrentFloor() == floorNumber && elevator.getId().equals(elevatorId)) {
                            log.info("JOIN QUEUE Passenger{id="+passenger.getId().substring(0,7) +
                                ", weight="+ passenger.getWeight()+
                                ", curFloor="+passenger.getCurrentFloor()+
                                ", expectedFloor="+passenger.getFloorToGo()+
                                " } wait on Lift{"+
                                elevator.getId().substring(0,7)+" }");
                            passengerCallback.addPassenger(passenger);
                        }
                    }
                }

                @Override
                public void executeCallback(PassengerManagerImpl.PassengerEvent value) {
                    onPassengerSelectedElevator(value.getPassenger(), value.getElevator());
                }
            });
        }
    }

    public void setPassengerCallback(PassengerCallback passengerCallback) {
        this.passengerCallback = passengerCallback;
    }

    public interface PassengerCallback
    {
        void addPassenger(Passenger passenger);
    }



}
