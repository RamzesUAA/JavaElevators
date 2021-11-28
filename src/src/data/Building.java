package data;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Building {
    private UUID uuid;
    private List<Floor> floorList;
    private List<Elevator> elevators;

    public Building(UUID uuid, List<Floor> floorList, List<Elevator> elevators) {
        this.uuid = uuid;
        this.floorList = floorList;
        this.elevators = elevators;
    }
}
