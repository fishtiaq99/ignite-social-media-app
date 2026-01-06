package com.ignite.controller;

import com.ignite.model.User;
import com.ignite.service.AdminService;
import com.ignite.util.StyleUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UserManagementController {

    private AdminService adminService;
    private ObservableList<User> usersData;
    private TableView<User> usersTable;

    public UserManagementController() {
        this.adminService = new AdminService();
        this.usersData = FXCollections.observableArrayList();
    }

    public VBox createUserManagementTab() {
        VBox mainContainer = new VBox(20);
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("User Management");
        titleLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 24px; -fx-font-weight: bold;");

        // Filter buttons
        HBox filterContainer = createFilterButtons();

        // Users table
        VBox tableContainer = createUsersTable();

        // Action buttons
        HBox actionButtons = createActionButtons();

        mainContainer.getChildren().addAll(titleLabel, filterContainer, tableContainer, actionButtons);

        // Load initial data
        loadAllUsers();

        return mainContainer;
    }

    private HBox createFilterButtons() {
        HBox filterContainer = new HBox(10);
        filterContainer.setAlignment(Pos.CENTER);

        Button allUsersBtn = createFilterButton("All Users", "ALL");
        Button unapprovedBtn = createFilterButton("Unapproved", "UNAPPROVED");
        Button activeBtn = createFilterButton("Active", "ACTIVE");
        Button inactiveBtn = createFilterButton("Inactive", "INACTIVE");

        filterContainer.getChildren().addAll(allUsersBtn, unapprovedBtn, activeBtn, inactiveBtn);
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

    private <T> void installStyledCellFactory(TableColumn<User, T> column,
                                              TableColumn<User, String> statusCol) {
        column.setCellFactory(tc -> new TableCell<User, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                setText(item.toString());

                // Alternate row colors
                boolean even = getIndex() % 2 == 0;
                String baseBg = even ? "#1a2d3f" : "#162536";

                // Default style for all cells
                setStyle(
                        "-fx-background-color: " + baseBg + ";" +
                                "-fx-text-fill: #e0e0e0;" +
                                "-fx-font-size: 12px;" +
                                "-fx-border-color: transparent;" +
                                "-fx-alignment: CENTER-LEFT;" +
                                "-fx-padding: 4 8 4 8;"
                );

                // Special coloring for Status column
                if (column == statusCol) {
                    String status = getText();
                    String textColor = "#e0e0e0";

                    switch (status) {
                        case "Active":
                            textColor = "#00ff88";
                            break;
                        case "Unapproved":
                            textColor = "#ffaa00";
                            break;
                        case "Inactive":
                            textColor = "#ff4444";
                            break;
                    }

                    setStyle(
                            "-fx-background-color: " + baseBg + ";" +
                                    "-fx-text-fill: " + textColor + ";" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-alignment: CENTER;" +
                                    "-fx-padding: 4 8 4 8;"
                    );
                }
            }
        });
    }


    private VBox createUsersTable() {
        VBox tableContainer = new VBox(10);
        tableContainer.setAlignment(Pos.TOP_CENTER);
        tableContainer.setMaxWidth(900);
        tableContainer.setPadding(new Insets(15));
        tableContainer.setStyle(
                "-fx-background-color: #0b1723;" +          // dark card background
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.25), 12,0,0,2);"
        );

        // Section label above the table
        Label tableHeader = new Label("User Accounts");
        tableHeader.setStyle(
                "-fx-text-fill: #00ff88;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 0 0 8 0;"
        );

        usersTable = new TableView<>();
        usersTable.setItems(usersData);
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usersTable.setPrefHeight(400);

        // Base table styling
        usersTable.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-table-cell-border-color: transparent;" +
                        "-fx-table-header-border-color: #00ff88;" +
                        "-fx-selection-bar: #003322;" +
                        "-fx-selection-bar-non-focused: #003322;"
        );

        // ID Column
        TableColumn<User, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getUserId()));
        idCol.setPrefWidth(70);

        // Username Column
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        usernameCol.setPrefWidth(140);

        // Email Column
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        emailCol.setPrefWidth(220);

        // Status Column
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(getUserStatus(c.getValue())));
        statusCol.setPrefWidth(120);

        // Join Date Column
        TableColumn<User, String> joinDateCol = new TableColumn<>("Joined");
        joinDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getJoinDate() != null
                        ? c.getValue().getJoinDate().toString().substring(0, 10)
                        : "N/A"
        ));
        joinDateCol.setPrefWidth(120);

        usersTable.getColumns().addAll(idCol, usernameCol, emailCol, statusCol, joinDateCol);

        // Header styles (the column titles)
        for (TableColumn<User, ?> column : usersTable.getColumns()) {
            column.setStyle(
                    "-fx-background-color: #0d1b2a;" +
                            "-fx-text-fill: #00ff88;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 13px;" +
                            "-fx-alignment: CENTER-LEFT;"
            );
        }

        // Pretty rows + colored status text
        installStyledCellFactory(idCol, statusCol);
        installStyledCellFactory(usernameCol, statusCol);
        installStyledCellFactory(emailCol, statusCol);
        installStyledCellFactory(statusCol, statusCol);
        installStyledCellFactory(joinDateCol, statusCol);

        // Row factory for nicer selection background
        usersTable.setRowFactory(tv -> new TableRow<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("-fx-background-color: transparent;");
                } else if (isSelected()) {
                    setStyle(
                            "-fx-background-color: linear-gradient(to right, #003300, #004466);" +
                                    "-fx-text-fill: #ffffff;"
                    );
                } else {
                    setStyle(""); // let cell factories handle background
                }
            }
        });

        tableContainer.getChildren().addAll(tableHeader, usersTable);
        return tableContainer;
    }


    private String getUserStatus(User user) {
        if (!user.isActive()) return "Inactive";
        if (!user.isApproved()) return "Unapproved";
        return "Active";
    }

    private HBox createActionButtons() {
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);

        Button approveBtn = StyleUtil.createPrimaryButton("Approve");
        Button deactivateBtn = createWarningButton("Deactivate");
        Button activateBtn = StyleUtil.createSecondaryButton("Activate");
        Button deleteBtn = createDangerButton("Delete");
        Button refreshBtn = StyleUtil.createSecondaryButton("Refresh");

        approveBtn.setOnAction(e -> handleApprove());
        deactivateBtn.setOnAction(e -> handleDeactivate());
        activateBtn.setOnAction(e -> handleActivate());
        deleteBtn.setOnAction(e -> handleDelete());
        refreshBtn.setOnAction(e -> loadAllUsers());

        buttonContainer.getChildren().addAll(approveBtn, deactivateBtn, activateBtn, deleteBtn, refreshBtn);
        return buttonContainer;
    }

    private Button createWarningButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #ffaa00;" +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 20; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: #ffcc44;" +
                                "-fx-text-fill: black; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 8 20; " +
                                "-fx-background-radius: 8; " +
                                "-fx-effect: dropshadow( gaussian , rgba(255,170,0,0.6) , 8,0,0,0 );"
                )
        );
        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: #ffaa00;" +
                                "-fx-text-fill: black; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 8 20; " +
                                "-fx-background-radius: 8;"
                )
        );

        return button;
    }

    private Button createDangerButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #ff4444;" +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 20; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: #ff6666;" +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 8 20; " +
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
                                "-fx-padding: 8 20; " +
                                "-fx-background-radius: 8;"
                )
        );

        return button;
    }

    private void handleFilter(String filterType) {
        switch (filterType) {
            case "ALL":
                loadAllUsers();
                break;
            case "UNAPPROVED":
                usersData.setAll(adminService.getUnapprovedUsers());
                break;
            case "ACTIVE":
                usersData.setAll(adminService.getActiveUsers());
                break;
            case "INACTIVE":
                usersData.setAll(adminService.getInactiveUsers());
                break;
        }
    }


    private void loadAllUsers() {
        usersData.setAll(adminService.getAllUsers());
    }

    private void handleApprove() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StyleUtil.showErrorAlert("No Selection", "Please select a user to approve.");
            return;
        }

        if (adminService.approveUser(selected.getUserId())) {
            StyleUtil.showSuccessAlert("Success", "User " + selected.getUsername() + " has been approved.");
            loadAllUsers();
        } else {
            StyleUtil.showErrorAlert("Error", "Failed to approve user.");
        }
    }

    private void handleDeactivate() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StyleUtil.showErrorAlert("No Selection", "Please select a user to deactivate.");
            return;
        }

        if (adminService.deactivateUser(selected.getUserId())) {
            StyleUtil.showSuccessAlert("Success", "User " + selected.getUsername() + " has been deactivated.");
            loadAllUsers();
        } else {
            StyleUtil.showErrorAlert("Error", "Failed to deactivate user.");
        }
    }

    private void handleActivate() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StyleUtil.showErrorAlert("No Selection", "Please select a user to activate.");
            return;
        }

        if (adminService.activateUser(selected.getUserId())) {
            StyleUtil.showSuccessAlert("Success", "User " + selected.getUsername() + " has been activated.");
            loadAllUsers();
        } else {
            StyleUtil.showErrorAlert("Error", "Failed to activate user.");
        }
    }

    private void handleDelete() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StyleUtil.showErrorAlert("No Selection", "Please select a user to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete user " + selected.getUsername() + "?");
        confirm.setContentText("This action is permanent and cannot be undone.");

        // Style the confirmation dialog
        confirm.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #ff4444; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (adminService.deleteUser(selected.getUserId())) {
                    StyleUtil.showSuccessAlert("Success", "User " + selected.getUsername() + " has been deleted.");
                    loadAllUsers();
                } else {
                    StyleUtil.showErrorAlert("Error", "Failed to delete user.");
                }
            }
        });
    }
}