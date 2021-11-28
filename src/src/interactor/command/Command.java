package interactor.command;

import lombok.*;
import data.*;
import java.util.List;

@Getter
@Setter
public class    Command {
    private int floorToGo;
    private List<Passenger> leavingPassengers;
    private List<Passenger> acceptingPassengers;

    public int getFloorToGo() {
        return floorToGo;
    }

    public void setFloorToGo(int floorToGo) {
        this.floorToGo = floorToGo;
    }

    public List<Passenger> getLeavingPassengers() {
        return leavingPassengers;
    }

    public void setLeavingPassengers(List<Passenger> leavingPassengers) {
        this.leavingPassengers = leavingPassengers;
    }

    public List<Passenger> getAcceptingPassengers() {
        return acceptingPassengers;
    }

    public void setAcceptingPassengers(List<Passenger> acceptingPassengers) {
        this.acceptingPassengers = acceptingPassengers;
    }

    public Command(int floorToGo, List<Passenger> leavingPassengers, List<Passenger> acceptingPassengers) {
        this.floorToGo = floorToGo;
        this.leavingPassengers = leavingPassengers;
        this.acceptingPassengers = acceptingPassengers;
    }
}
