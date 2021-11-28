package interactor;

import common.Observable;
import data.Building;
import data.Elevator;
import viewModels.BuildingParameters;

import java.util.HashMap;
import java.util.Map;

public class BuildingManagerImpl extends Observable<BuildingManager.BuildingCallback> implements BuildingManager {

    private Building currBuilding;

    private Map<String, ElevatorManagerImpl> elevatorManagerMap;

    public void initialize(BuildingParameters buildingParameters)
    {
        this.elevatorManagerMap = new HashMap<>();
        this.currBuilding = buildingParameters.getBuilding();
        for(Elevator elevator : buildingParameters.getBuilding().getElevators())
            elevatorManagerMap.put(elevator.getId(), new ElevatorManagerImpl(elevator, buildingParameters));

        notifyOnBuildingCreated(buildingParameters.getBuilding());
    }

    public Map<String, ElevatorManagerImpl> getElevatorManagerMap() {
        return elevatorManagerMap;
    }

    public Building getCurrBuilding() {
        return currBuilding;
    }

    public void setCurrBuilding(Building currBuilding) {
        this.currBuilding = currBuilding;
    }

    private void notifyOnBuildingCreated(Building building)
    {
        for(BuildingCallback buildingCallback : callbackSet)
            buildingCallback.onBuildingCreated(building);
    }

    @Override
    public void dispose() {
        elevatorManagerMap.forEach((s, elevatorManager) -> elevatorManager.dispose());
    }
}
