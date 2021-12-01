package start;

import di.MainModule;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import pages.StartPageControl;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

@Log4j
public class Main extends Application {

    private static Injector injector;

    public static Injector getInjector() {
        return injector;
    }

    public static void setInjector(Injector injector) {
        Main.injector = injector;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = new StartPageControl();
        primaryStage.setTitle("liftEr");
        primaryStage.setResizable(false);
        var scene = new Scene(root,700,500, Color.CHOCOLATE);
        scene.setFill(Color.web("#81c483"));
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }


    public static void main(String[] args) {
        injector = Guice.createInjector(new MainModule());

        launch(args);
    }
}
