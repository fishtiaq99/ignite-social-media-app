package com.ignite.controller;

import com.ignite.model.Admin;
import com.ignite.service.AuthService;
import com.ignite.util.StyleUtil;
import com.ignite.util.SessionManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminLoginController {

    private Stage primaryStage;
    private AuthService authService = new AuthService();

    public AdminLoginController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public VBox createLoginScreen() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        StyleUtil.applyDarkTheme(root);

        Label title = StyleUtil.createTitleLabel("Admin Login");

        TextField usernameField = StyleUtil.createInputField("Username");
        PasswordField passwordField = StyleUtil.createPasswordField("Password");

        Button loginButton = StyleUtil.createPrimaryButton("Login");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            Admin admin = authService.loginAdmin(username, password);

            if (admin == null) {
                StyleUtil.showErrorAlert("Login Failed", "Invalid username or password.");
            } else {
                // Set current admin in session
                SessionManager.setCurrentAdmin(admin);


                // Show success alert
                StyleUtil.showSuccessAlert("Success", "Login successful!");

                // Navigate to admin dashboard
                AdminDashboardController controller = new AdminDashboardController(primaryStage);
                VBox dashboard = controller.createDashboard();
                switchScene(dashboard);
            }
        });

        Button backButton = StyleUtil.createSecondaryButton("Back");
        backButton.setOnAction(e -> {
            WelcomeController welcome = new WelcomeController(primaryStage);
            VBox welcomeScreen = welcome.createWelcomeScreen();
            Scene welcomeScene = new Scene(welcomeScreen, 1000, 700);
            primaryStage.setScene(welcomeScene);
        });


        root.getChildren().addAll(title, usernameField, passwordField, loginButton, backButton);

        return root;
    }

    private void switchScene(VBox newRoot) {
        Scene newScene = new Scene(newRoot, 1000, 700);
        primaryStage.setScene(newScene);
    }
}
