package controls;

import data.Passenger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import start.Main;
import viewModels.ElevatorViewModel;

import java.io.IOException;

public class ElevatorControl extends VBox {
    private final ObservableList<FloorControl> floorControls = FXCollections.observableArrayList();
    private final ElevatorViewModel elevatorViewModel;

    public ElevatorControl(String elevatorId, int floorCount) {
        elevatorViewModel = Main.getInjector().getInstance(ElevatorViewModel.class);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../src/controls/ElevatorControl.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setMinWidth(200);
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vBox);
        HBox hBox = new HBox();
        var output = new Label("Вихід");
        HBox.setMargin(output, new Insets(0,0,0,10));
        var input = new Label("Вхід");
        input.widthProperty().addListener((observableValue, number, t1) -> {
            hBox.setPrefWidth(getWidth());
            var v = getWidth() - input.getWidth() - output.getWidth() - 20;
            HBox.setMargin(input, new Insets(0,0,0,v));
        });
        widthProperty().addListener((observableValue, number, t1) -> {
            hBox.setPrefWidth(getWidth());
            var v = getWidth() - input.getWidth() - output.getWidth() - 20;
            HBox.setMargin(input, new Insets(0,0,0,v));
        });
        hBox.getChildren().addAll(output,input);
        getChildren().addAll(hBox, scrollPane);

        elevatorViewModel.init(elevatorId);

        for (int i = 0; i < floorCount; i++)
            floorControls.add(new FloorControl(i, elevatorId));

        floorControls.sort((f1, f2) -> f2.getFloorNumber() - f1.getFloorNumber());
        vBox.getChildren().addAll(floorControls);

        elevatorViewModel.elevatorStateProperty().addListener((state, newState) -> Platform.runLater(() -> {

            if (state != null && state.getCurrentFloor()!=newState.getCurrentFloor()) {
                floorControls.stream().filter(floorControl -> floorControl.getFloorNumber() == state.getCurrentFloor())
                        .findFirst()
                        .ifPresent(FloorControl::SetElevatorNotThere);
            }

            if (newState != null) {
                floorControls.stream().filter(floorControl -> floorControl.getFloorNumber() == newState.getCurrentFloor())
                        .findFirst()
                        .ifPresent(floorControl -> floorControl
                                .SetElevatorThere(newState.getPassengersCount(),
                                        newState.getMaxWeight(),
                                        newState.getCurrentWeight(),
                                        newState.getColor(),
                                        elevatorViewModel.getColor()));
            }

            if (newState != null && newState.getAcceptPassengers() != null) {
                newState.getAcceptPassengers().stream().map(Passenger::getId).forEach(id -> floorControls
                        .stream()
                        .filter(floorControl -> floorControl.getFloorNumber() == newState.getCurrentFloor())
                        .findFirst()
                        .ifPresent(floorControl -> floorControl.removePassengerFromWaiting(id)));
            }

            if (newState != null && newState.getLeavingPassengers() != null) {
                newState.getLeavingPassengers().forEach(passenger -> floorControls
                        .stream()
                        .filter(floorControl -> floorControl.getFloorNumber() == newState.getCurrentFloor())
                        .findFirst()
                        .ifPresent(floorControl -> floorControl.addPassengerToLeaving(passenger)));
            }
        }));
    }
}
