package com.imagecrypto;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        GUI gui = new GUI();
        Scene scene = new Scene(gui.createContent(primaryStage), 600, 500); // Pass window
        primaryStage.setTitle("Image to Audio Encryptor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
