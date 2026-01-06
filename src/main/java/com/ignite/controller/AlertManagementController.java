package com.ignite.controller;

import com.ignite.model.SystemAlert;
import com.ignite.model.enums.AlertType;
import com.ignite.service.AdminService;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.text.SimpleDateFormat;
import java.util.Collections;

public class AlertManagementController {

    private final AdminService adminService;
    private final ObservableList<SystemAlert> alertsData;
    private TableView<SystemAlert> alertsTable;
    private Label totalAlertsValueLabel;
    private Label activeAlertsValueLabel;
    private Label urgentAlertsValueLabel;

    public AlertManagementController() {
        this.adminService = new AdminService();
        this.alertsData = FXCollections.observableArrayList();
    }

    public VBox createAlertManagementTab() {
        // Smaller spacing & padding so more room for the table
        VBox mainContainer = new VBox(10);
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(10, 15, 10, 15));

        Label title = new Label("Alert Management");
        title.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 24px; -fx-font-weight: bold;");


        HBox statsContainer = createStatsCards();
        HBox filterContainer = createFilterButtons();

        VBox tableContainer = createAlertsTable();   // left
        VBox actionButtons = createActionButtons();  // right

        HBox alertsSection = new HBox(20);
        alertsSection.setAlignment(Pos.TOP_CENTER);
        alertsSection.getChildren().addAll(tableContainer, actionButtons);

        // move the whole table+buttons block slightly up
        alertsSection.setTranslateY(-10);

        mainContainer.getChildren().addAll(title, statsContainer, filterContainer, alertsSection);

        loadAllAlerts();

        return mainContainer;
    }



    private HBox createStatsCards() {
        HBox statsContainer = new HBox(12);
        statsContainer.setAlignment(Pos.CENTER);

        // initial counts
        int totalAlerts = adminService.getTotalAlertsCount();
        int activeAlerts = adminService.getActiveAlertsCount();
        int urgentAlerts = adminService.getUrgentAlertsCount();

        // create value labels and keep references in fields
        totalAlertsValueLabel = new Label(String.valueOf(totalAlerts));
        activeAlertsValueLabel = new Label(String.valueOf(activeAlerts));
        urgentAlertsValueLabel = new Label(String.valueOf(urgentAlerts));

        VBox totalCard  = createStatCard("Total Alerts",  totalAlertsValueLabel,  "#00ff88");
        VBox activeCard = createStatCard("Active Alerts", activeAlertsValueLabel, "#44aaff");
        VBox urgentCard = createStatCard("Urgent Alerts", urgentAlertsValueLabel, "#ff4444");

        statsContainer.getChildren().addAll(totalCard, activeCard, urgentCard);
        return statsContainer;
    }

    private VBox createStatCard(String title, Label valueLabel, String color) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: #1a2d3f;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        card.setPrefSize(130, 60);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");

        // style the value label we passed in
        valueLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #223344;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, " + color + "33, 8,0,0,0);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #1a2d3f;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;"
        ));

        return card;
    }

    private void refreshStats() {
        int totalAlerts = adminService.getTotalAlertsCount();
        int activeAlerts = adminService.getActiveAlertsCount();
        int urgentAlerts = adminService.getUrgentAlertsCount();

        if (totalAlertsValueLabel != null) {
            totalAlertsValueLabel.setText(String.valueOf(totalAlerts));
        }
        if (activeAlertsValueLabel != null) {
            activeAlertsValueLabel.setText(String.valueOf(activeAlerts));
        }
        if (urgentAlertsValueLabel != null) {
            urgentAlertsValueLabel.setText(String.valueOf(urgentAlerts));
        }
    }


    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: #1a2d3f;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        card.setPrefSize(130, 60);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #223344;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, " + color + "33, 8,0,0,0);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #1a2d3f;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;"
        ));

        return card;
    }

    private HBox createFilterButtons() {
        HBox filterContainer = new HBox(10);
        filterContainer.setAlignment(Pos.CENTER);

        Button allAlertsBtn = createFilterButton("All Alerts", "ALL");
        Button activeBtn = createFilterButton("Active", "ACTIVE");
        Button urgentBtn = createFilterButton("Urgent", "URGENT");
        Button recentBtn = createFilterButton("Recent", "RECENT");
        Button byTypeBtn = createFilterButton("By Type", "TYPE");

        filterContainer.getChildren().addAll(allAlertsBtn, activeBtn, urgentBtn, recentBtn, byTypeBtn);
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

        button.setOnAction(e -> handleFilter(filterType));
        return button;
    }

    private VBox createAlertsTable() {
        VBox tableContainer = new VBox(8);
        tableContainer.setAlignment(Pos.TOP_CENTER);
        tableContainer.setMaxWidth(900);
        // less padding so it sits higher
        tableContainer.setStyle("-fx-padding: 0 20 0 20; -fx-background-color: transparent;");

        alertsTable = new TableView<>();
        alertsTable.setItems(alertsData);
        alertsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        alertsTable.setMaxWidth(Double.MAX_VALUE);

        // smaller height so buttons fit fully
        alertsTable.setPrefHeight(260);
        alertsTable.setMinHeight(220);
        alertsTable.setFocusTraversable(false);

        alertsTable.setStyle(
                "-fx-background-color: #1a2d3f; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-padding: 6; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.25), 12,0,0,3);"
        );

        // === COLUMNS ===
        TableColumn<SystemAlert, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getAlertId()));
        idCol.setPrefWidth(60);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<SystemAlert, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAlertType().toString()
        ));
        typeCol.setPrefWidth(100);
        typeCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<SystemAlert, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getMessage() != null
                        ? c.getValue().getMessage().substring(0, Math.min(60, c.getValue().getMessage().length())) + "..."
                        : "No message"
        ));
        messageCol.setPrefWidth(350);

        TableColumn<SystemAlert, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isActive() ? "Active" : "Inactive"
        ));
        statusCol.setPrefWidth(90);
        statusCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<SystemAlert, String> dateCol = new TableColumn<>("Created");
        dateCol.setCellValueFactory(c -> {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
            return new SimpleStringProperty(
                    c.getValue().getCreationDate() != null
                            ? df.format(c.getValue().getCreationDate())
                            : "N/A"
            );
        });
        dateCol.setPrefWidth(120);
        dateCol.setStyle("-fx-alignment: CENTER;");

        alertsTable.getColumns().setAll(idCol, typeCol, messageCol, statusCol, dateCol);

        // === HEADER STYLING ===
        for (TableColumn<SystemAlert, ?> col : alertsTable.getColumns()) {
            col.setStyle(
                    "-fx-background-color: #0d1b2a; " +
                            "-fx-text-fill: #00ff88; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 13px; " +
                            "-fx-border-color: #00ff88; " +
                            "-fx-border-width: 0 0 1 0; " +
                            "-fx-alignment: CENTER-LEFT;"
            );
        }

        // === CELL STYLING ===
        applyStyledCellFactory(idCol, typeCol, statusCol);
        applyStyledCellFactory(typeCol, typeCol, statusCol);
        applyStyledCellFactory(messageCol, typeCol, statusCol);
        applyStyledCellFactory(statusCol, typeCol, statusCol);
        applyStyledCellFactory(dateCol, typeCol, statusCol);

        // === SCROLLBAR STYLING ===
        alertsTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Node vBar = alertsTable.lookup(".scroll-bar:vertical");
                if (vBar != null) {
                    vBar.setStyle(
                            "-fx-background-color: #0d1b2a; " +
                                    "-fx-background-radius: 0;"
                    );
                }

                Node thumb = alertsTable.lookup(".scroll-bar:vertical .thumb");
                if (thumb != null) {
                    thumb.setStyle(
                            "-fx-background-color: #00ff88; " +
                                    "-fx-background-radius: 4;"
                    );
                }
            }
        });

        tableContainer.getChildren().add(alertsTable);
        return tableContainer;
    }



    private <T> void applyStyledCellFactory(
            TableColumn<SystemAlert, T> column,
            TableColumn<SystemAlert, String> typeCol,
            TableColumn<SystemAlert, String> statusCol
    ) {
        column.setCellFactory(tc -> new TableCell<SystemAlert, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #1a2d3f;");
                    return;
                }

                setText(item.toString());

                boolean even = (getIndex() % 2 == 0);
                String baseBg = even ? "#1a2d3f" : "#162536";

                setStyle(
                        "-fx-background-color: " + baseBg + ";" +
                                "-fx-text-fill: #e0e0e0;" +
                                "-fx-font-size: 12px;" +
                                "-fx-border-color: transparent;" +
                                "-fx-alignment: CENTER-LEFT;"
                );

                if (column == typeCol) {
                    String type = item.toString();
                    String color;

                    switch (type) {
                        case "URGENT":
                            color = "#ff4444";
                            break;
                        case "SAFETY":
                            color = "#ffaa00";
                            break;
                        case "MAINTENANCE":
                            color = "#44aaff";
                            break;
                        case "ANNOUNCEMENT":
                        default:
                            color = "#00ff88";
                            break;
                    }

                    setStyle(
                            "-fx-background-color: " + baseBg + ";" +
                                    "-fx-text-fill: " + color + ";" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-alignment: CENTER;"
                    );
                }

                if (column == statusCol) {
                    String status = item.toString();
                    String color = status.equals("Active") ? "#00ff88" : "#ff4444";

                    setStyle(
                            "-fx-background-color: " + baseBg + ";" +
                                    "-fx-text-fill: " + color + ";" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-alignment: CENTER;"
                    );
                }
            }
        });
    }

    private VBox createActionButtons() {
        VBox buttonColumn = new VBox(12);
        buttonColumn.setAlignment(Pos.TOP_CENTER);
        // remove extra top padding and shift column up a bit
        buttonColumn.setStyle("-fx-padding: 0 0 0 0;");
        buttonColumn.setTranslateY(-30);   // <-- lift buttons visually

        Button sendAlertBtn = StyleUtil.createPrimaryButton("New");
        Button viewDetailsBtn = StyleUtil.createSecondaryButton("Details");
        Button toggleStatusBtn = StyleUtil.createSecondaryButton("Toggle");
        Button deleteBtn = createDangerButton("Delete");
        Button refreshBtn = StyleUtil.createSecondaryButton("Refresh");

        // Make the buttons wider so text is not cut
        double BTN_WIDTH = 150;

        sendAlertBtn.setPrefWidth(BTN_WIDTH);
        viewDetailsBtn.setPrefWidth(BTN_WIDTH);
        toggleStatusBtn.setPrefWidth(BTN_WIDTH);
        deleteBtn.setPrefWidth(BTN_WIDTH);
        refreshBtn.setPrefWidth(BTN_WIDTH);

// Slightly more internal padding so text fits comfortably
        sendAlertBtn.setPadding(new Insets(12, 20, 12, 20));
        viewDetailsBtn.setPadding(new Insets(12, 20, 12, 20));
        toggleStatusBtn.setPadding(new Insets(12, 20, 12, 20));
        deleteBtn.setPadding(new Insets(12, 20, 12, 20));
        refreshBtn.setPadding(new Insets(12, 20, 12, 20));


        sendAlertBtn.setOnAction(e -> handleSendAlert());
        viewDetailsBtn.setOnAction(e -> handleViewDetails());
        toggleStatusBtn.setOnAction(e -> handleToggleStatus());
        deleteBtn.setOnAction(e -> handleDeleteAlert());
        refreshBtn.setOnAction(e -> loadAllAlerts());

        buttonColumn.getChildren().addAll(
                sendAlertBtn,
                viewDetailsBtn,
                toggleStatusBtn,
                deleteBtn,
                refreshBtn
        );

        return buttonColumn;
    }


    private Button createDangerButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #ff4444;" +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: #ff6666;" +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 20; " +
                                "-fx-background-radius: 8; " +
                                "-fx-effect: dropshadow( gaussian , rgba(255,68,68,0.6) , 8,0,0,0 );"
                )
        );
        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: #ff4444;" +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 20; " +
                                "-fx-background-radius: 8;"
                )
        );

        return button;
    }

    private void handleFilter(String filterType) {
        switch (filterType) {
            case "ALL": {
                loadAllAlerts();
                break;
            }
            case "ACTIVE": {
                var list = adminService.getActiveAlerts();
                alertsData.setAll(list != null ? list : Collections.emptyList());
                break;
            }
            case "URGENT": {
                var list = adminService.getUrgentAlerts();
                alertsData.setAll(list != null ? list : Collections.emptyList());
                break;
            }
            case "RECENT": {
                var list = adminService.getRecentAlerts(50);
                alertsData.setAll(list != null ? list : Collections.emptyList());
                break;
            }
            case "TYPE": {
                showTypeFilterDialog();
                break;
            }
        }
    }

    private void showTypeFilterDialog() {
        ChoiceDialog<AlertType> dialog = new ChoiceDialog<>(AlertType.ANNOUNCEMENT, AlertType.values());
        dialog.setTitle("Filter by Type");
        dialog.setHeaderText("Select Alert Type");
        dialog.setContentText("Choose the alert type:");

        dialog.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        dialog.showAndWait().ifPresent(alertType ->
                alertsData.setAll(adminService.getAlertsByType(alertType))
        );
    }

    private void loadAllAlerts() {
        var all = adminService.getAllAlerts();
        if (all == null) {
            alertsData.clear();
        } else {
            alertsData.setAll(all);
        }
        // update the counts on the stat cards
        refreshStats();
    }

    private void handleSendAlert() {
        Dialog<SystemAlert> alertDialog = new Dialog<>();
        alertDialog.setTitle("Send System Alert");
        alertDialog.setHeaderText("Create a new system alert");

        ButtonType sendButtonType = new ButtonType("Send Alert", ButtonBar.ButtonData.OK_DONE);
        alertDialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        grid.setStyle("-fx-background-color: #1a2d3f;");

        Label typeLabel = new Label("Alert Type:");
        typeLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        ComboBox<AlertType> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(AlertType.values());
        typeComboBox.setValue(AlertType.ANNOUNCEMENT);
        typeComboBox.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white;");

        Label messageLabel = new Label("Message:");
        messageLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter alert message (max 500 characters)");
        messageArea.setWrapText(true);
        messageArea.setPrefHeight(120);
        messageArea.setStyle("-fx-control-inner-background: #0d1b2a; -fx-text-fill: white; -fx-border-color: #00ff88;");

        grid.add(typeLabel, 0, 0);
        grid.add(typeComboBox, 1, 0);
        grid.add(messageLabel, 0, 1);
        grid.add(messageArea, 1, 1);

        alertDialog.getDialogPane().setContent(grid);
        alertDialog.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        alertDialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButtonType) {
                if (messageArea.getText().trim().isEmpty()) {
                    StyleUtil.showErrorAlert("Validation Error", "Please fill in the message.");
                    return null;
                }

                if (messageArea.getText().trim().length() > 500) {
                    StyleUtil.showErrorAlert("Validation Error", "Message cannot exceed 500 characters.");
                    return null;
                }

                SystemAlert alert = new SystemAlert();
                alert.setAdminId(SessionManager.getCurrentAdmin().getAdminId());
                alert.setMessage(messageArea.getText().trim());
                alert.setAlertType(typeComboBox.getValue());
                alert.setActive(true);

                return alert;
            }
            return null;
        });

        alertDialog.showAndWait().ifPresent(alert -> {
            if (adminService.sendAlert(alert)) {
                StyleUtil.showSuccessAlert("Alert Sent", "System alert has been sent successfully to all users!");
                loadAllAlerts();
            } else {
                StyleUtil.showErrorAlert("Error", "Failed to send alert.");
            }
        });
    }

    private void handleViewDetails() {
        SystemAlert selected = alertsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StyleUtil.showErrorAlert("No Selection", "Please select an alert to view details.");
            return;
        }

        Alert detailsDialog = new Alert(Alert.AlertType.INFORMATION);
        detailsDialog.setTitle("Alert Details");
        detailsDialog.setHeaderText("Alert #" + selected.getAlertId());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a2d3f;");

        Label typeLabel = new Label("Type: " + selected.getAlertType().toString());
        typeLabel.setStyle("-fx-text-fill: " + getAlertTypeColor(selected.getAlertType()) + "; -fx-font-weight: bold;");

        Label statusLabel = new Label("Status: " + (selected.isActive() ? "Active" : "Inactive"));
        statusLabel.setStyle("-fx-text-fill: " + (selected.isActive() ? "#00ff88" : "#ff4444") + "; -fx-font-weight: bold;");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
        Label dateLabel = new Label("Created: " +
                (selected.getCreationDate() != null ? dateFormat.format(selected.getCreationDate()) : "N/A"));
        dateLabel.setStyle("-fx-text-fill: #cccccc;");

        Label messageLabel = new Label("Message:");
        messageLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        TextArea messageText = new TextArea(selected.getMessage());
        messageText.setEditable(false);
        messageText.setWrapText(true);
        messageText.setPrefHeight(150);
        messageText.setStyle("-fx-control-inner-background: #0d1b2a; -fx-text-fill: white; -fx-border-color: #00ff88;");

        content.getChildren().addAll(typeLabel, statusLabel, dateLabel, messageLabel, messageText);

        detailsDialog.getDialogPane().setContent(content);
        detailsDialog.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        detailsDialog.showAndWait();
    }

    private String getAlertTypeColor(AlertType alertType) {
        switch (alertType) {
            case URGENT:
                return "#ff4444";
            case SAFETY:
                return "#ffaa00";
            case MAINTENANCE:
                return "#44aaff";
            case ANNOUNCEMENT:
            default:
                return "#00ff88";
        }
    }

    private void handleToggleStatus() {
        SystemAlert selected = alertsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StyleUtil.showErrorAlert("No Selection", "Please select an alert to toggle status.");
            return;
        }

        String newStatus = selected.isActive() ? "inactive" : "active";
        String action = selected.isActive() ? "deactivate" : "activate";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Toggle Alert Status");
        confirm.setHeaderText("Are you sure you want to " + action + " this alert?");
        confirm.setContentText("The alert will be " + newStatus + " for all users.");

        confirm.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                boolean success;
                if (selected.isActive()) {
                    success = adminService.deactivateAlert(selected.getAlertId());
                } else {
                    success = adminService.activateAlert(selected.getAlertId());
                }

                if (success) {
                    StyleUtil.showSuccessAlert("Status Updated", "Alert has been " + newStatus + " successfully.");
                    loadAllAlerts();
                } else {
                    StyleUtil.showErrorAlert("Error", "Failed to update alert status.");
                }
            }
        });
    }

    private void handleDeleteAlert() {
        SystemAlert selected = alertsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StyleUtil.showErrorAlert("No Selection", "Please select an alert to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Alert");
        confirm.setHeaderText("Are you sure you want to delete this alert?");
        confirm.setContentText("This action cannot be undone. The alert will be permanently removed from the system.");

        confirm.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #ff4444; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (adminService.deleteAlert(selected.getAlertId())) {
                    StyleUtil.showSuccessAlert("Alert Deleted", "Alert has been deleted successfully.");
                    loadAllAlerts();
                } else {
                    StyleUtil.showErrorAlert("Error", "Failed to delete alert.");
                }
            }
        });
    }
}
