package java2_Proj_45;
/**
 * Main Class for the program
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("quakeViewer.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Earth Quake Viewer");
        primaryStage.setScene(new Scene(root, 977, 900));
        primaryStage.show();

    }
}
