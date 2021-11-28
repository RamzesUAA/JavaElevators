package viewModels;

import data.Building;
import data.Speed;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BuildingParameters {
    private Building building;
    private Speed speed;
    private int passengerCreationDelay;
    private Map<Class, Color> strategyColorMap;
    private int strategyNum;
    private int elevatorCount;
    private int floorCount;

    public BuildingParameters() {
        strategyColorMap = new HashMap<>();
    }
}
