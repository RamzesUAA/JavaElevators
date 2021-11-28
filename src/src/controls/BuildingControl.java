package controls;

import viewModels.BuildingParameters;
import viewModels.BuildingViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import start.Main;

import java.io.IOException;

public class BuildingControl extends ScrollPane {

    private ObservableList<ElevatorControl> elevatorControls = FXCollections.observableArrayList();

    private BuildingViewModel buildingViewModel = Main.getInjector().getInstance(BuildingViewModel.class);

    @FXML
    private HBox hBox;

    public BuildingControl(@NotNull BuildingParameters buildingParameters) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../src/controls/BuildingControl.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        widthProperty().addListener((obs, oldVal,newVal)->{
            hBox.setPrefWidth((Double) newVal);
            elevatorControls.forEach(elevatorControl -> elevatorControl.setPrefWidth((Double) newVal / buildingParameters.getElevatorCount()));
        });
        heightProperty().addListener((obs, oldVal,newVal)-> hBox.setMaxHeight((Double) newVal));
        setFitToWidth(true);
        setFitToHeight(true);

        buildingViewModel.buildingStateProperty().addListener((state, newState) -> {
            elevatorControls.clear();

            System.out.println("Elevators " + newState.getElevatorIds());
            for (int i = 0; i < newState.getElevatorCount(); i++)
                elevatorControls.add(new ElevatorControl(
                        newState.getElevatorIds().get(i),
                        newState.getFloorCount()));

            hBox.getChildren().addAll(elevatorControls);
        });


    }


}
