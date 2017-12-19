package quakeViewer;

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
        Parent root = FXMLLoader.load(getClass().getResource("quakeViewer.fxml"));
        primaryStage.setTitle("Earth Quake Viewer");
        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.show();
        dataLoader dl = new dataLoader();
    }
}
