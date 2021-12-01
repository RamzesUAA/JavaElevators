package controls;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class PassengerControl extends Rectangle {
    String id;

    public PassengerControl(String id, int floorToGo, double weight){
        this.id = id;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../src/controls/PassengerControl.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setWidth(50);
        setHeight(50);
        var r = new Random(id.hashCode());
        var s = new Image(getClass().getResource("../src/controls/Girl.png").toExternalForm());
        setFill(new ImagePattern(s));

        var tooltip = new Tooltip(String.format("My id = %s \nMy weight = %.2f \nI want to get to the %d th floor",
                id,
                weight,
                floorToGo + 1));
        tooltip.setShowDelay(new Duration(10));
        tooltip.setShowDuration(Duration.hours(1));
        tooltip.setAutoFix(true);
        Tooltip.install(this, tooltip);
    }

    public String getID() {
        return id;
    }
}
