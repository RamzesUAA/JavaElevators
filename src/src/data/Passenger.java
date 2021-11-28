package data;

import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class Passenger {
    private String Id;               //ідентифікатор пасажира, унікальний
    private Size size;            //габарити пасажира
    private double weight;        //вага пасажира
    private int currentFloor;     //номер поверху на якому стоїть пасажир
    private int floorToGo;        //номер поверху на який пасажир має намір поїхати

    public Passenger(Size size, double weight, int currentFloor, int floorToGo) {
        Id = UUID.randomUUID().toString();
        this.size = size;
        this.weight = weight;
        this.currentFloor = currentFloor;
        this.floorToGo = floorToGo;
    }

    public Elevator chooseElevator(Building building) {
        Floor floor = building.getFloorList().get(currentFloor);
        Map<Elevator, Queue<Passenger>> values = floor.getPassengerQueues();
        Elevator elevator = null;

        if(values.size() == 0)
        {
            elevator = building.getElevators().get(0);
        } else {
            int minSize = values
                    .entrySet()
                    .stream()
                    .min(Comparator.comparingInt(l->l.getValue().size()))
                    .orElseThrow(() -> new RuntimeException("There aren't elevators"))
                    .getValue().size();
            var minQueueElevators = values
                    .entrySet()
                    .stream()
                    .filter(l->l.getValue().size()==minSize)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            Random rnd = new Random();
            elevator = minQueueElevators.get(rnd.nextInt(minQueueElevators.size()));
        }
        building.getFloorList()
                .get(getCurrentFloor())
                .getPassengerQueues()
                .get(elevator)
                .add(this);
        int size = building
                .getFloorList()
                .get(getCurrentFloor())
                .getPassengerQueues()
                .get(elevator)
                .size();
        //Replace with some value
        if(size>3)
            log.info("THERE WAIT Passengers{count="+size+", floor="+getCurrentFloor()+"}waiting for the Lift{id="+elevator.getId().substring(0,7)+"}");

        return elevator;
    }
}
