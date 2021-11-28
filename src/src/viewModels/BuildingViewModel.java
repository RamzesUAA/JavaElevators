package viewModels;

import com.google.inject.Inject;
import common.ObjectProperty;
import data.Elevator;
import interactor.BuildingManager;
import interactor.BuildingManagerImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class BuildingViewModel {

    private final common.ObjectProperty<State> buildingState = new common.ObjectProperty<>(new State());

    @Inject
    public BuildingViewModel(BuildingManager buildingManager, BuildingParameters buildingParameters) {
        if(buildingManager instanceof BuildingManagerImpl)
        {
            ((BuildingManagerImpl)buildingManager).subscribe(building -> {
                State tmp = buildingState.getValue();
                if(tmp != null)
                {
                    tmp.elevatorCount = building.getElevators().size();
                    tmp.floorCount = building.getFloorList().size();
                    tmp.elevatorIds = building.getElevators()
                            .stream()
                            .map(Elevator::getId)
                            .collect(Collectors.toList());
                    buildingState.setValue(tmp);
                }
            });
            ((BuildingManagerImpl) buildingManager).initialize(buildingParameters);
        }
    }

    public ObjectProperty<State> buildingStateProperty() {
        return buildingState;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class State
    {
        private int floorCount = 0;
        private int elevatorCount = 0;
        private List<String> elevatorIds = null;
    }

}
