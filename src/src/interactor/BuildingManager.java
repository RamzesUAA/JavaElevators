package interactor;

import common.Disposable;
import data.Building;
import data.Elevator;

import java.util.Map;

public interface BuildingManager extends Disposable {

    Building getCurrBuilding();

    void setCurrBuilding(Building currBuilding);

    Map<String, ElevatorManagerImpl> getElevatorManagerMap();

    interface BuildingCallback {
        void onBuildingCreated(Building building);
    }
}
