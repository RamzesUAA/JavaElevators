package controls;

import viewModels.PassengerViewModel;
import data.Passenger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import start.Main;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FloorControl extends ScrollPane {
    private int floorNumber;
    private Tooltip tooltip;
    private StackPane stack;
    private Text floor;
    private Text passengerCountView;
    private Text currentWightView;

    @FXML
    private HBox hBox;

    private HBox waitingPassengersBox;
    private HBox leavingPassengersBox;
    private final Timer timer;

    private final Rectangle rect;

    public FloorControl(int number, String elevatorId) {
        floorNumber = number;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../src/controls/FloorControl.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        ScrollPane leavingPassengersPane = new ScrollPane();
        ScrollPane waitingPassengersPane = new ScrollPane();
        leavingPassengersPane.setFitToHeight(true);
        leavingPassengersPane.setFitToWidth(true);
        waitingPassengersPane.setFitToHeight(true);
        waitingPassengersPane.setFitToWidth(true);
        waitingPassengersBox = new HBox();
        leavingPassengersBox = new HBox();
        leavingPassengersBox.setAlignment(Pos.TOP_RIGHT);
        waitingPassengersPane.setContent(waitingPassengersBox);
        leavingPassengersPane.setContent(leavingPassengersBox);
        widthProperty().addListener((observableValue, number1, t1) -> {
            var width = (getWidth() - stack.getWidth()) / 2;
            leavingPassengersPane.setPrefWidth(width);
            waitingPassengersPane.setPrefWidth(width);
        });
        setMinViewportHeight(55);
        setFitToHeight(true);
        setFitToWidth(true);
        rect = new Rectangle(50, 50, Color.DIMGRAY);
        rect.setArcHeight(5);
        rect.setArcWidth(5);
        rect.setStroke(Color.grayRgb(1));
        rect.setStrokeType(StrokeType.INSIDE);
        floor = new Text(String.valueOf(floorNumber + 1));
        stack = new StackPane();
        stack.setMaxSize(55, 55);
        stack.getChildren().addAll(rect, floor);
        hBox.getChildren().addAll(leavingPassengersPane, stack, waitingPassengersPane);
        this.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        PassengerViewModel passengerViewModel = Main.getInjector().getInstance(PassengerViewModel.class);
        passengerViewModel.setPassengerCallback(FloorControl.this::addPassengerToWaiting);
        passengerViewModel.init(elevatorId, floorNumber);
        timer = new Timer();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void SetElevatorThere(int _passengerCount, double maxWeight, double _currentWeight, Color weightColor, Color elevatorColor) {
        Platform.runLater(() -> {
            tooltip = new Tooltip(String.format("Passenger count: %d\nMax weight: %.2f\nCurrent weight: %.2f", _passengerCount, maxWeight, _currentWeight));
            tooltip.setShowDelay(new Duration(10));
            tooltip.setShowDuration(Duration.hours(1));
            tooltip.setAutoFix(true);
            rect.setFill(elevatorColor); // TODO: Set image instead of color
            stack.getChildren().removeAll(passengerCountView, currentWightView);
            passengerCountView = new Text(String.valueOf(_passengerCount));
            passengerCountView.setStyle("-fx-padding: 20px");
            currentWightView = new Text(String.valueOf(_currentWeight));
            currentWightView.setFill(weightColor);
            currentWightView.setStyle("-fx-margin: 20px");
            StackPane.setAlignment(passengerCountView, Pos.TOP_LEFT);
            StackPane.setMargin(passengerCountView, new Insets(3));
            StackPane.setAlignment(currentWightView, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(currentWightView, new Insets(3));
            stack.getChildren().addAll(passengerCountView, currentWightView);
            Tooltip.install(stack, tooltip);
        });
    }

    public void SetElevatorNotThere() {
        Platform.runLater(() -> {
            rect.setFill(Color.DIMGRAY);
            stack.getChildren().removeAll(passengerCountView, currentWightView);
            Tooltip.uninstall(stack, tooltip);
        });
    }

    public void addPassengerToWaiting(Passenger passenger) {
        Platform.runLater(() -> waitingPassengersBox.getChildren().add(new PassengerControl(passenger.getId(), passenger.getFloorToGo(), passenger.getWeight())));

    }

    public void removePassengerFromWaiting(String id) {
        waitingPassengersBox.getChildren().stream()
                .filter(node -> node instanceof PassengerControl && ((PassengerControl) node).getID().equals(id))
                .findFirst()
                .ifPresent(node -> waitingPassengersBox.getChildren().remove(node));
    }

    public void addPassengerToLeaving(Passenger passenger) {
        Platform.runLater(() -> {
            leavingPassengersBox.getChildren().add(new PassengerControl(passenger.getId(), passenger.getFloorToGo(), passenger.getWeight()));

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    removePassengerFromLeaving(passenger.getId());
                }
            }, 5000);
        });

    }

    public void removePassengerFromLeaving(String id) {
        Platform.runLater(() -> leavingPassengersBox.getChildren().stream()
                .filter(node -> node instanceof PassengerControl && ((PassengerControl) node).getID().equals(id))
                .findFirst()
                .ifPresent(node -> leavingPassengersBox.getChildren().remove(node)));
    }
}
