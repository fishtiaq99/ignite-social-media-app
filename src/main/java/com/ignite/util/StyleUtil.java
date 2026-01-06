package com.ignite.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.PasswordField;

public class StyleUtil {

    // Apply overall dark blue-green gradient background
    public static void applyDarkTheme(VBox container) {
        container.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #000000, #0d1b2a, #003300);" +
                        "-fx-padding: 40;" +
                        "-fx-alignment: center;"
        );
    }

    // Title Label with neon green-blue glow effect
    public static Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 40px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: linear-gradient(from 0% 0% to 100% 100%, #00ff88, #00d4ff);" +
                        "-fx-effect: dropshadow( gaussian , rgba(0,255,136,0.8) , 12,0,0,0 );"
        );
        return label;
    }

    // Subtitle Label with subtle neon glow
    public static Label createSubtitleLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-text-fill: #cccccc; " +
                        "-fx-padding: 0 0 25 0; " +
                        "-fx-effect: dropshadow( gaussian , rgba(0,212,255,0.4) , 4,0,0,0 );"
        );
        return label;
    }

    // Primary button with black-blue-green gradient
    public static Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: linear-gradient(to right, #003300, #0066ff, #00ff88);" +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12 35; " +
                        "-fx-background-radius: 12; " +
                        "-fx-cursor: hand;"
        );

        // Hover effect with glow
        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: linear-gradient(to right, #00ff88, #0066ff, #003300);" +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 12 35; " +
                                "-fx-background-radius: 12; " +
                                "-fx-effect: dropshadow( gaussian , rgba(0,255,136,0.6) , 15,0,0,0 );"
                )
        );
        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: linear-gradient(to right, #003300, #0066ff, #00ff88);" +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 12 35; " +
                                "-fx-background-radius: 12;"
                )
        );

        return button;
    }

    // Secondary button with neon green border
    public static Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #00ff88; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 25; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 12; " +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: #00ff88; " +
                                "-fx-text-fill: black; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 25; " +
                                "-fx-border-color: #00ff88; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 12; " +
                                "-fx-effect: dropshadow( gaussian , rgba(0,255,136,0.6) , 12,0,0,0 );"
                )
        );
        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-text-fill: #00ff88; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 25; " +
                                "-fx-border-color: #00ff88; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 12;"
                )
        );

        return button;
    }

    // TextField with neon green-blue border on focus
    public static TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 12 15; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #404040; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 10;"
        );

        // Focused neon effect
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                        "-fx-background-color: #0d1b2a; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 12 15; " +
                                "-fx-background-radius: 10; " +
                                "-fx-border-color: #00ff88; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 10;" +
                                "-fx-effect: dropshadow( gaussian , rgba(0,255,136,0.6) , 10,0,0,0 );"
                );
            } else {
                field.setStyle(
                        "-fx-background-color: #0d1b2a; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 12 15; " +
                                "-fx-background-radius: 10; " +
                                "-fx-border-color: #404040; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 10;"
                );
            }
        });

        return field;
    }

    // Error Alert with neon green-red vibe
    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #ff3c78; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );
        alert.getDialogPane().lookup(".content.label").setStyle(
                "-fx-text-fill: #ff3c78; -fx-font-size: 14px;"
        );

        alert.showAndWait();
    }

    // Success Alert with neon green-blue vibe
    public static void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );
        alert.getDialogPane().lookup(".content.label").setStyle(
                "-fx-text-fill: #00ff88; -fx-font-size: 14px;"
        );

        alert.showAndWait();
    }

    public static TextField createInputField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setStyle(
                "-fx-background-color: #222;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #888;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 8;"
        );
        field.setMaxWidth(300);
        return field;
    }

    public static PasswordField createPasswordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        field.setStyle(
                "-fx-background-color: #222;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #888;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 8;"
        );
        field.setMaxWidth(300);
        return field;
    }

    public static void styleDashboardTab(Tab tab) {
        // base style
        tab.setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-text-fill: #cccccc; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 10;"
        );

        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) {
                tab.setStyle(
                        "-fx-background-color: linear-gradient(to right, #003300, #0066ff, #00ff88); " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 10; " +
                                "-fx-padding: 10 18;"
                );
            } else {
                tab.setStyle(
                        "-fx-background-color: #0d1b2a; " +
                                "-fx-text-base-color: #cccccc; " +
                                "-fx-background-radius: 10;"
                );
            }
        });
    }

    public static String getTabStyle(boolean selected) {
        if (selected) {
            return "-fx-background-color: linear-gradient(to right, #003300, #0066ff, #00ff88);" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 10 18;";
        } else {
            return "-fx-background-color: #0d1b2a;" +
                    "-fx-text-fill: #cccccc;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 8 15;";
        }
    }



}
