package com.ignite.controller;

import com.ignite.service.UserService;
import com.ignite.util.StyleUtil;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentSignupController {

    private Stage primaryStage;

    public StudentSignupController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public VBox createSignupScreen() {
        VBox mainContainer = new VBox();
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(20);

        // Title
        Label titleLabel = new Label("Student Sign Up");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");

        // Form fields
        TextField usernameField = StyleUtil.createStyledTextField("Username");
        TextField emailField = StyleUtil.createStyledTextField("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(StyleUtil.createStyledTextField("").getStyle());

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setStyle(StyleUtil.createStyledTextField("").getStyle());

        TextArea bioField = new TextArea();
        bioField.setPromptText("Bio (optional)");
        bioField.setWrapText(true);
        bioField.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-control-inner-background: #0d1b2a;");

        // Buttons
        Button signupButton = StyleUtil.createPrimaryButton("Sign Up");
        Button backButton = StyleUtil.createSecondaryButton("Back");

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(signupButton, backButton);

        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        formContainer.setStyle("-fx-padding: 20;");
        formContainer.getChildren().addAll(
                titleLabel, usernameField, emailField, passwordField,
                confirmPasswordField, bioField, buttonContainer
        );

        mainContainer.getChildren().add(formContainer);

        // Button actions
        signupButton.setOnAction(e -> handleSignup(
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText(),
                bioField.getText()
        ));

        backButton.setOnAction(e -> handleBack());

        return mainContainer;
    }

    private void handleSignup(String username, String email, String password,
                              String confirmPassword, String bio) {
        try {
            // Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                StyleUtil.showErrorAlert("Error", "Please fill in all required fields");
                return;
            }

            if (!password.equals(confirmPassword)) {
                StyleUtil.showErrorAlert("Error", "Passwords do not match");
                return;
            }

            if (password.length() < 6) {
                StyleUtil.showErrorAlert("Error", "Password must be at least 6 characters");
                return;
            }

            // Register user
            UserService userService = new UserService();
            userService.registerUser(username, email, password, bio);

            StyleUtil.showSuccessAlert("Registration Submitted",
                    "Your registration has been submitted for admin approval. " +
                            "You will be able to login once your account is approved.");

            handleBack();

        } catch (Exception e) {
            StyleUtil.showErrorAlert("Registration Failed", e.getMessage());
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
