package com.ignite.controller;

import com.ignite.util.SessionManager;
import com.ignite.controller.ReportsTabController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentDashboardController {

    private Stage primaryStage;
    private TabPane tabPane;

    public StudentDashboardController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public VBox createDashboard() {
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #0d1b2a, #001100, #001a33);"
        );

        // Header
        Label welcomeLabel = new Label("Welcome to Ignite, " +
                (SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getUsername() : "Student") + "!");
        welcomeLabel.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-text-fill: #00ff88;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.4), 4,0,0,2);"
        );

        // TabPane without ugly grey background
        tabPane = new TabPane();
        tabPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-tab-min-height: 32;" +
                        "-fx-tab-max-height: 32;" +
                        "-fx-tab-min-width: 120;" +
                        "-fx-tab-max-width: 120;" +
                        "-fx-padding: 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-tab-header-background: transparent;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-background: navy-blue;" +
                        "-fx-border-color: transparent;"
        );

        // Create tabs
        // Create tabs
        Tab homeTab = createHomeTab();
        Tab createPostTab = createCreatePostTab();
        Tab feedTab = createFeedTab();
        Tab followManagementTab = createFollowManagementTab();
        Tab reportsTab = createReportsTab();
        Tab alertsTab = createAlertsTab();
        Tab inquiriesTab = createInquiriesTab();
        Tab editProfileTab = createEditProfileTab(); // Add this line
        Tab searchTab = createSearchTab();

// Add all tabs to TabPane
        tabPane.getTabs().addAll(homeTab, createPostTab, feedTab, followManagementTab,
                reportsTab, alertsTab, searchTab, inquiriesTab, editProfileTab);


        for (Tab tab : tabPane.getTabs()) {
            styleTab(tab, tab.isSelected());
            tab.setOnSelectionChanged(e -> styleTab(tab, tab.isSelected()));
        }

        //tabPane.getTabs().addAll(homeTab, createPostTab, feedTab, followManagementTab); // Include the new tab

        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #00ff88;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 6 12;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;"
        );
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(
                "-fx-background-color: #00ff88;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 6 12;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;"
        ));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #00ff88;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 6 12;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;"
        ));
        logoutButton.setOnAction(e -> handleLogout());

        // Align logout button to right
        HBox logoutContainer = new HBox(logoutButton);
        logoutContainer.setAlignment(Pos.CENTER_RIGHT);
        logoutContainer.setPadding(new Insets(0, 20, 0, 0));

        mainContainer.getChildren().addAll(welcomeLabel, tabPane, logoutContainer);
        return mainContainer;
    }

    private void styleTab(Tab tab, boolean selected) {
        if (selected) {
            tab.setStyle(
                    "-fx-background-color: linear-gradient(to right, #003300, #0066ff, #00ff88);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14;" +
                            "-fx-padding: 6 12;" +
                            "-fx-background-radius: 12;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.3), 6,0,0,2);"
            );
        } else {
            tab.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #00ff88;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14;" +
                            "-fx-padding: 6 12;"
            );
        }
    }

    private Tab createHomeTab() {
        HomeTabController controller = new HomeTabController();
        return controller.createTab();
    }

    private Tab createCreatePostTab() {
        CreatePostTabController controller = new CreatePostTabController();
        return controller.createTab();
    }

    private Tab createFeedTab() {
        FeedTabController controller = new FeedTabController();
        return controller.createTab();
    }

    private void handleLogout() {
        SessionManager.logout();
        WelcomeController welcomeController = new WelcomeController(primaryStage);
        VBox welcomeScreen = welcomeController.createWelcomeScreen();
        switchScene(welcomeScreen);
    }

    private void switchScene(VBox newRoot) {
        Scene newScene = new Scene(newRoot, 1200, 800);
        primaryStage.setScene(newScene);
    }

    private Tab createFollowManagementTab() {
        FollowManagementController controller = new FollowManagementController();
        return controller.createTab();
    }
    private Tab createReportsTab() {
        ReportsTabController controller = new ReportsTabController();
        return controller.createTab();
    }
    private Tab createAlertsTab() {
        AlertsTabController controller = new AlertsTabController();
        return controller.createTab();
    }

    private Tab createInquiriesTab() {
        InquiriesTabController controller = new InquiriesTabController();
        return controller.createTab();
    }

    private Tab createEditProfileTab() {
        EditProfileTabController controller = new EditProfileTabController();
        return controller.createTab();
    }

    private Tab createSearchTab() {
        SearchController controller = new SearchController(primaryStage);
        VBox searchScreen = controller.createSearchScreen();

        Tab searchTab = new Tab("Search");
        searchTab.setClosable(false);
        searchTab.setContent(searchScreen);
        return searchTab;
    }
}