package com.ignite;

import com.ignite.controller.WelcomeController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create the welcome controller and pass the stage
        WelcomeController welcomeController = new WelcomeController(primaryStage);

        // Get the root node from the controller
        Scene scene = new Scene(welcomeController.createWelcomeScreen(), 1000, 700);

        primaryStage.setTitle("Ignite - Social Media Platform");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}