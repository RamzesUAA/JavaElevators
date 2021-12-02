package pages;

import com.google.inject.Guice;
import data.*;
import di.MainModule;
import interactor.BuildingManager;
import interactor.PassengerManager;
import interactor.strategies.FirstStrategy;
import interactor.strategies.SecondStrategy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Window;
import start.Controller;
import start.Main;
import viewModels.BuildingParameters;

import java.io.IOException;
import java.util.*;

import static javax.swing.text.StyleConstants.Background;

// Structure:
// Scene |
//       | VBox - vertical container
//              | HBox - horizontal container
//              | HBox - horizontal container
//              | HBox - horizontal container



public class StartPageControl extends VBox {
    private TextField liftCountField;
    private TextField floorCountField;
    private TextField passengerCreationField;
    private Color firstStrategyColor;
    private Color secondStrategyColor;
    private int selected = 1;
    private BuildingParameters buildingParameters;
    private static Random r = new Random();

    private static final String STANDARD_BUTTON_STYLE = "-fx-background-color: #FF7F50; -fx-border-color: grey; -fx-border-radius: 5;";
    private static final String HOVERED_BUTTON_STYLE  = "-fx-background-color: #ff5a1d ; -fx-border-color: grey; -fx-border-radius: 5;";

    public StartPageControl(){
        buildingParameters = Main.getInjector().getInstance(BuildingParameters.class);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../src/pages/StartPage.fxml"));
//        var loader = new FXMLLoader(getClass().getResource("../src/pages/TractionElevator.png"));
//        var sqq =  new Image(getClass().getResource("../src/pages/TractionElevator.png").toExternalForm());

        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        // Background set
//        BackgroundFill s= new BackgroundFill()
        BackgroundFill backgroundFill =
                new BackgroundFill(
                        Color.valueOf("#FFA07A"),
                        new CornerRadii(0),
                        new Insets(0)
                );
        javafx.scene.layout.Background background =
                new Background(backgroundFill);
        setBackground(background);


        // hBox - for horizontal align of elements
        HBox hBox = new HBox();
        setPadding(new Insets(5,5,5,5));
        FlowPane paramsPane = new FlowPane(Orientation.VERTICAL, 10, 10);
        FlowPane strategyPane = new FlowPane(Orientation.VERTICAL, 10, 10);

        HBox.setMargin(strategyPane, new Insets(0,0,0,5));
        Label liftCountLabel = new Label("Кількість ліфтів:");
        liftCountLabel.setFont(new Font(12));
        Label floorCountLabel = new Label("Кількість поверхів:");
        Label liftSpeedLabel = new Label("Швидкість ліфтів:");
        Label passengerCreationLabel = new Label("Максимальний час появи пасажира (мс):");

        liftCountField = new TextField("10");
        liftCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                liftCountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            else if(!newValue.equals("") && Integer.parseInt(newValue) > 10) {
                liftCountField.setText(String.valueOf(10));
            }
        });

        floorCountField = new TextField("10");

        floorCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                floorCountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            else if(!newValue.equals("") && Integer.parseInt(newValue) > 100) {
                floorCountField.setText(String.valueOf(100));
            }
        });

        passengerCreationField = new TextField("1500");
        passengerCreationField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                liftCountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            else if(!newValue.equals("") && Integer.parseInt(newValue) > 10000) {
                liftCountField.setText(String.valueOf(10000));
            }
        });


        liftCountField.setPrefWidth(80);
        floorCountField.setPrefWidth(80);
        passengerCreationField.setPrefWidth(80);


        HBox liftCountBox = new HBox();
        liftCountBox.getChildren().addAll(liftCountLabel, liftCountField);
        HBox floorCountBox = new HBox();
        floorCountBox.getChildren().addAll(floorCountLabel, floorCountField);
        HBox speedBox = new HBox();
        ObservableList<String> speedList = FXCollections.observableArrayList("Низька", "Середня", "Висока");
        ChoiceBox<String> speedChoice = new ChoiceBox<>(speedList);
        speedChoice.valueProperty().addListener((observableValue, s, t1) -> {
            switch (speedList.indexOf(t1)) {
                case 0:
                    buildingParameters.setSpeed(Speed.Slow);
                    break;
                case 1:
                    buildingParameters.setSpeed(Speed.Medium);
                    break;
                case 2:
                    buildingParameters.setSpeed(Speed.High);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + speedList.indexOf(s));
            }
        });

        speedChoice.setValue(speedList.get(1));
        speedBox.getChildren().addAll(liftSpeedLabel, speedChoice);
        HBox.setMargin(passengerCreationLabel, new Insets(3,5,0,0));
        passengerCreationLabel.widthProperty().addListener((observableValue, oldNumber, newNumber) -> {
            var v1 = passengerCreationLabel.getWidth() - liftCountLabel.getWidth();
            var v2 = passengerCreationLabel.getWidth() - floorCountLabel.getWidth();
            var v3 = passengerCreationLabel.getWidth() - liftSpeedLabel.getWidth();
            HBox.setMargin(liftCountLabel, new Insets(3, v1 + 5, 0, 0));
            HBox.setMargin(floorCountLabel, new Insets(3, v2 + 5, 0, 0));
            HBox.setMargin(liftSpeedLabel, new Insets(3, v3 + 5, 0, 0));
        });
        HBox passengerBox = new HBox();
        passengerBox.getChildren().addAll(passengerCreationLabel, passengerCreationField);


        HBox starBox = new HBox();
        starBox.getChildren().addAll(passengerCreationLabel, passengerCreationField);

        paramsPane.getChildren().addAll(liftCountBox, floorCountBox, speedBox, passengerBox, starBox);
        hBox.getChildren().add(paramsPane);

        RadioButton firstStrategyBtn = new RadioButton("Перша стратегія");
        RadioButton secondStrategyBtn = new RadioButton("Друга стратегія");
        RadioButton randomStrategyBtn = new RadioButton("Рандомні стратегії");
        ToggleGroup group = new ToggleGroup();
        firstStrategyBtn.setToggleGroup(group);
        secondStrategyBtn.setToggleGroup(group);
        randomStrategyBtn.setToggleGroup(group);
        firstStrategyBtn.setOnAction(event -> selected = 1);
        secondStrategyBtn.setOnAction(event -> selected = 2);
        randomStrategyBtn.setOnAction(event -> selected = 3);



        firstStrategyBtn.widthProperty().addListener((observableValue, oldNumber, newNumber) -> {
            if(firstStrategyBtn.getWidth()!=0) {
                var v1 = randomStrategyBtn.getWidth() - firstStrategyBtn.getWidth();
                HBox.setMargin(firstStrategyBtn, new Insets(0, v1 + 5, 0, 0));
            }
        });
        secondStrategyBtn.widthProperty().addListener((observableValue, oldNumber, newNumber) -> {
            if(firstStrategyBtn.getWidth()!=0){
            var v2 = randomStrategyBtn.getWidth() - secondStrategyBtn.getWidth();
            HBox.setMargin(secondStrategyBtn, new Insets(0, v2 + 5, 0, 0));
            }
        });
        firstStrategyBtn.setSelected(true);


        HBox firstStrategyBox = new HBox();
        firstStrategyBox.getChildren().addAll(firstStrategyBtn);



        HBox secondStrategyBox = new HBox();
        secondStrategyBox.getChildren().addAll(secondStrategyBtn);
        strategyPane.getChildren().addAll(firstStrategyBox,secondStrategyBox);


        strategyPane.getChildren().add(randomStrategyBtn);
        hBox.getChildren().add(strategyPane);
        hBox.setPrefHeight(150);

         // Here we set containers alignment to center
        hBox.setAlignment(Pos.CENTER);
        setSpacing(5);
        setAlignment(Pos.CENTER);
        getChildren().add(hBox);

        // Button styles
        Button start = new Button("Поїхали!");
        Font font = Font.font("Courier New", FontWeight.BOLD, 25);
        start.setFont(font);
        start.setStyle(STANDARD_BUTTON_STYLE);

        start.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                start.setStyle(HOVERED_BUTTON_STYLE);
            }
        });
        start.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                start.setStyle(STANDARD_BUTTON_STYLE);
            }
        });

        start.setOnAction(this::OnStart);
        getChildren().add(start);
    }



    private static void ToStart(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.SPACE) {
            Main.getInjector().getInstance(BuildingManager.class).dispose();
            Main.getInjector().getInstance(PassengerManager.class).dispose();
            Main.setInjector(Guice.createInjector(new MainModule()));
            Parent root1 = new StartPageControl();
            Optional<Stage> stage1View = Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst().map(it -> (Stage) it );
            stage1View.ifPresent(stage -> {
                stage.setMaximized(false);
                stage.setScene(new Scene(root1,500,175));
                stage.setResizable(false);
            });
        }
    }

    private void OnStart(ActionEvent actionEvent) {
        buildingParameters.getStrategyColorMap().put(FirstStrategy.class, firstStrategyColor);
        buildingParameters.getStrategyColorMap().put(SecondStrategy.class, secondStrategyColor);

        var elevatorCount = Integer.parseInt(liftCountField.getText());
        var floorCount = Integer.parseInt(floorCountField.getText());
        List<Elevator> elevators = new ArrayList<>();

        for (int i = 0; i < elevatorCount; i++) {
            var maxPassengerCount = r.nextInt(33) + 5;
            elevators.add(new Elevator(maxPassengerCount * 80,
                    new Size(150, 210),
                    maxPassengerCount,
                    buildingParameters.getSpeed().getSpeed(),
                    10));
        }
        List<Floor> floorList = new ArrayList<>();

        for (int i = 0; i < floorCount; i++)
            floorList.add(new Floor(i, elevators));
        buildingParameters.setBuilding(new Building(UUID.randomUUID(), floorList, elevators));
        buildingParameters.setElevatorCount(elevatorCount);
        buildingParameters.setFloorCount(floorCount);
        buildingParameters.setStrategyNum(selected);
        buildingParameters.setPassengerCreationDelay(Integer.parseInt(passengerCreationField.getText()));

        Parent root = new Controller();
        Stage stage = (Stage) getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(StartPageControl::ToStart);
        stage.setScene(scene);


//        stage.setMaximized(true);
//        stage.setResizable(true);
    }
}
