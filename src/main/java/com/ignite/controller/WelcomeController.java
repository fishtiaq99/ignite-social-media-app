package com.ignite.controller;

import com.ignite.util.StyleUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WelcomeController {

    private Stage primaryStage;

    // Constructor that accepts the stage
    public WelcomeController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public VBox createWelcomeScreen() {
        VBox mainContainer = new VBox();

        // Apply styling
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(20);

        // Create title
        Label titleLabel = StyleUtil.createTitleLabel("IGNITE");
        Label subtitleLabel = StyleUtil.createSubtitleLabel("Social Media Platform");

        // Create buttons
        Button studentLoginButton = StyleUtil.createPrimaryButton("Student Login");
        Button studentSignupButton = StyleUtil.createPrimaryButton("Student Sign Up");
        Button adminLoginButton = StyleUtil.createSecondaryButton("Admin Login");

        // Set button actions
        studentLoginButton.setOnAction(e -> handleStudentLogin());
        studentSignupButton.setOnAction(e -> handleStudentSignup());
        adminLoginButton.setOnAction(e -> handleAdminLogin());

        // Create button container
        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setMaxWidth(400);
        buttonContainer.setStyle("-fx-padding: 20;");

        // Add student section
        Label studentLabel = new Label("Student Access:");
        studentLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");
        buttonContainer.getChildren().addAll(studentLabel, studentLoginButton, studentSignupButton);

        // Add admin section
        Label adminLabel = new Label("Admin Access:");
        adminLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-padding: 20 0 0 0;");
        buttonContainer.getChildren().addAll(adminLabel, adminLoginButton);

        // Add everything to main container
        mainContainer.getChildren().addAll(titleLabel, subtitleLabel, buttonContainer);

        return mainContainer;
    }

    private void handleStudentLogin() {
        System.out.println("Student Login clicked");
        // Navigate to student login screen
        StudentLoginController loginController = new StudentLoginController(primaryStage);
        VBox loginScreen = loginController.createLoginScreen();
        switchScene(loginScreen);
    }

    private void handleStudentSignup() {
        System.out.println("Student Signup clicked");
        // Navigate to student signup screen
        StudentSignupController signupController = new StudentSignupController(primaryStage);
        VBox signupScreen = signupController.createSignupScreen();
        switchScene(signupScreen);
    }

    private void handleAdminLogin() {
        AdminLoginController loginController = new AdminLoginController(primaryStage);
        VBox loginScreen = loginController.createLoginScreen();
        switchScene(loginScreen);
    }


    private void switchScene(VBox newRoot) {
        Scene newScene = new Scene(newRoot, 1000, 700);
        primaryStage.setScene(newScene);
    }
}