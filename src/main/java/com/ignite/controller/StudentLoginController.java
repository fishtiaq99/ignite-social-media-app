package com.ignite.controller;

import com.ignite.service.AuthService;
import com.ignite.model.User;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentLoginController {

    private Stage primaryStage;

    public StudentLoginController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public VBox createLoginScreen() {
        VBox mainContainer = new VBox();

        // Apply styling
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(20);

        // Create title
        Label titleLabel = new Label("Student Login");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");

        // Create form fields
        TextField usernameField = StyleUtil.createStyledTextField("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(StyleUtil.createStyledTextField("").getStyle());

        // Create buttons
        Button loginButton = StyleUtil.createPrimaryButton("Login");
        Button backButton = StyleUtil.createSecondaryButton("Back");

        // Button container
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        // Set button actions
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        backButton.setOnAction(e -> handleBack());

        buttonContainer.getChildren().addAll(loginButton, backButton);

        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        formContainer.setStyle("-fx-padding: 20;");

        formContainer.getChildren().addAll(titleLabel, usernameField, passwordField, buttonContainer);
        mainContainer.getChildren().add(formContainer);

        return mainContainer;
    }

    private void handleLogin(String username, String password) {
        try {
            if (username.isEmpty() || password.isEmpty()) {
                StyleUtil.showErrorAlert("Error", "Please fill in all fields");
                return;
            }

            AuthService authService = new AuthService();
            User user = authService.login(username, password);

            if (!user.isApproved()) {
                StyleUtil.showErrorAlert("Account Pending", "Your account is pending admin approval. Please wait for approval before logging in.");
                return;
            }

            SessionManager.setCurrentUser(user);
            StyleUtil.showSuccessAlert("Success", "Login successful!");

            // Navigate to dashboard
            StudentDashboardController dashboardController = new StudentDashboardController(primaryStage);
            VBox dashboard = dashboardController.createDashboard();
            switchScene(dashboard);

        } catch (Exception e) {
            StyleUtil.showErrorAlert("Login Failed", e.getMessage());
        }
    }

    private void handleBack() {
        WelcomeController welcomeController = new WelcomeController(primaryStage);
        VBox welcomeScreen = welcomeController.createWelcomeScreen();
        switchScene(welcomeScreen);
    }

    private void switchScene(VBox newRoot) {
        Scene newScene = new Scene(newRoot, 1000, 700);
        primaryStage.setScene(newScene);
    }
}