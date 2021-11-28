package start;


import controls.BuildingControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import viewModels.BuildingParameters;

import java.io.IOException;

public class Controller extends StackPane {

    @FXML
    BuildingControl buildingControl;

    public Controller() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../src/start/sample.fxml"));

        BuildingParameters buildingParameters = Main.getInjector().getInstance(BuildingParameters.class);

        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setAlignment(Pos.TOP_CENTER);
        buildingControl = new BuildingControl(buildingParameters);
        getChildren().add(buildingControl);
        widthProperty().addListener((obs, oldVal, newVal) ->
            buildingControl.setPrefWidth((Double) newVal));
        heightProperty().addListener((obs, oldVal, newVal) ->
            buildingControl.setMaxHeight((Double) newVal));

    }
}
