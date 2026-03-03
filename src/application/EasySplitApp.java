package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EasySplitApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {try {
        // Load login screen initially
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("EasySplit - Login");
        primaryStage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

