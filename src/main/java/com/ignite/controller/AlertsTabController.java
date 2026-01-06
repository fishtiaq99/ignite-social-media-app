package com.ignite.controller;

import com.ignite.model.SystemAlert;
import com.ignite.model.enums.AlertType;
import com.ignite.service.AlertService;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class AlertsTabController {

    private AlertService alertService;
    private VBox alertsContainer;

    public AlertsTabController() {
        this.alertService = new AlertService();
    }

    public Tab createTab() {
        Tab alertsTab = new Tab("Alerts");
        alertsTab.setClosable(false);

        VBox mainContainer = new VBox();
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(20));

        // Title
        Label titleLabel = StyleUtil.createTitleLabel("System Alerts");

        // Filter buttons
        HBox filterContainer = createFilterButtons();

        // Alerts container
        // Alerts container (scrollable)
        alertsContainer = new VBox(15);
        alertsContainer.setAlignment(Pos.TOP_CENTER);
        alertsContainer.setMaxWidth(700);
        alertsContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(alertsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

// style to match dark theme
        scrollPane.setStyle(
                "-fx-background: transparent; " +
                        "-fx-background-color: transparent; "
        );
        scrollPane.getViewportBounds(); // ensures background is transparent

        // Refresh button
        Button refreshButton = StyleUtil.createSecondaryButton("Refresh Alerts");
        refreshButton.setOnAction(e -> loadAlerts("ALL"));

        mainContainer.getChildren().addAll(
                titleLabel,
                filterContainer,
                scrollPane,
                refreshButton
        );


        // Load initial alerts
        loadAlerts("ALL");

        alertsTab.setContent(mainContainer);
        return alertsTab;
    }

    private HBox createFilterButtons() {
        HBox filterContainer = new HBox(10);
        filterContainer.setAlignment(Pos.CENTER);

        Button allButton = createFilterButton("All Alerts", "ALL");
        Button urgentButton = createFilterButton("Urgent", "URGENT");
        Button announcementButton = createFilterButton("Announcements", "ANNOUNCEMENT");
        Button maintenanceButton = createFilterButton("Maintenance", "MAINTENANCE");
        Button safetyButton = createFilterButton("Safety", "SAFETY");

        filterContainer.getChildren().addAll(
                allButton, urgentButton, announcementButton, maintenanceButton, safetyButton
        );

        return filterContainer;
    }

    private Button createFilterButton(String text, String filterType) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #1a2d3f; " +
                        "-fx-text-fill: #00ff88; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: #00ff88; " +
                                "-fx-text-fill: black; " +
                                "-fx-font-size: 12px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 8 15; " +
                                "-fx-border-color: #00ff88; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 8;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: #1a2d3f; " +
                                "-fx-text-fill: #00ff88; " +
                                "-fx-font-size: 12px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 8 15; " +
                                "-fx-border-color: #00ff88; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 8;"
                )
        );

        button.setOnAction(e -> loadAlerts(filterType));
        return button;
    }

    private void loadAlerts(String filterType) {
        alertsContainer.getChildren().clear();

        List<SystemAlert> alerts;

        switch (filterType) {
            case "URGENT":
                alerts = alertService.getUrgentAlerts();
                break;
            case "ANNOUNCEMENT":
                alerts = alertService.getAlertsByType(AlertType.ANNOUNCEMENT);
                break;
            case "MAINTENANCE":
                alerts = alertService.getAlertsByType(AlertType.MAINTENANCE);
                break;
            case "SAFETY":
                alerts = alertService.getAlertsByType(AlertType.SAFETY);
                break;
            default:
                alerts = alertService.getAllAlertsSorted();
                break;
        }

        if (alerts.isEmpty()) {
            Label noAlertsLabel = new Label("No " + (filterType.equals("ALL") ? "" : filterType.toLowerCase()) + " alerts found");
            noAlertsLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 16px; -fx-padding: 40;");
            alertsContainer.getChildren().add(noAlertsLabel);
            return;
        }

        for (SystemAlert alert : alerts) {
            alertsContainer.getChildren().add(createAlertCard(alert));
        }
    }

    private VBox createAlertCard(SystemAlert alert) {
        VBox alertCard = new VBox(10);
        alertCard.setStyle(
                "-fx-background-color: " + getAlertColor(alert.getAlertType()) + ";" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + getAlertBorderColor(alert.getAlertType()) + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;"
        );
        alertCard.setMaxWidth(650);

        // Header with type and date
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setSpacing(10);

        // Alert type badge
        Label typeLabel = new Label(alert.getAlertType().toString());
        typeLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 3 8; " +
                        "-fx-background-color: " + getAlertBadgeColor(alert.getAlertType()) + ";" +
                        "-fx-background-radius: 8;"
        );

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
        Label dateLabel = new Label(dateFormat.format(alert.getCreationDate()));
        dateLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 12px;");

        HBox.setHgrow(dateLabel, Priority.ALWAYS);
        headerRow.getChildren().addAll(typeLabel, dateLabel);

        // Alert message
        Label messageLabel = new Label(alert.getMessage());
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);

        // Urgent indicator for urgent/safety alerts
        if (alert.isUrgent()) {
            HBox urgentIndicator = createUrgentIndicator();
            alertCard.getChildren().addAll(headerRow, urgentIndicator, messageLabel);
        } else {
            alertCard.getChildren().addAll(headerRow, messageLabel);
        }

        return alertCard;
    }

    private HBox createUrgentIndicator() {
        HBox urgentBox = new HBox(5);
        urgentBox.setAlignment(Pos.CENTER_LEFT);

        // Warning icon (using text as icon)
        Label warningIcon = new Label("⚠️");
        warningIcon.setStyle("-fx-font-size: 14px;");

        Label urgentText = new Label("URGENT ALERT");
        urgentText.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px; -fx-font-weight: bold;");

        urgentBox.getChildren().addAll(warningIcon, urgentText);
        return urgentBox;
    }

    private String getAlertColor(AlertType alertType) {
        switch (alertType) {
            case URGENT:
                return "#4a1a1a"; // Dark red
            case SAFETY:
                return "#1a3a4a"; // Dark blue
            case MAINTENANCE:
                return "#2a4a1a"; // Dark green
            case ANNOUNCEMENT:
                return "#2a1a4a"; // Dark purple
            default:
                return "#1a2d3f"; // Default dark blue
        }
    }

    private String getAlertBorderColor(AlertType alertType) {
        switch (alertType) {
            case URGENT:
                return "#ff4444"; // Bright red
            case SAFETY:
                return "#44aaff"; // Bright blue
            case MAINTENANCE:
                return "#44ff88"; // Bright green
            case ANNOUNCEMENT:
                return "#aa44ff"; // Bright purple
            default:
                return "#00ff88"; // Default neon green
        }
    }

    private String getAlertBadgeColor(AlertType alertType) {
        switch (alertType) {
            case URGENT:
                return "#ff4444"; // Bright red
            case SAFETY:
                return "#44aaff"; // Bright blue
            case MAINTENANCE:
                return "#44ff88"; // Bright green
            case ANNOUNCEMENT:
                return "#aa44ff"; // Bright purple
            default:
                return "#00ff88"; // Default neon green
        }
    }
}