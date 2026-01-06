package com.ignite.controller;

import com.ignite.model.User;
import com.ignite.model.ResolvedReport;
import com.ignite.service.AdminService;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.text.SimpleDateFormat;

public class AdminDashboardController {

    private final Stage primaryStage;
    private final AdminService adminService;
    private TabPane tabPane;
    // dashboard stat value labels
    private Label totalUsersValueLabel;
    private Label unapprovedUsersValueLabel;
    private Label totalPostsValueLabel;
    private Label reportedPostsValueLabel;
    private Label reportedCommentsValueLabel;
    private Label pendingInquiriesValueLabel;


    // Table data for unapproved users
    private final ObservableList<User> unapprovedUsers = FXCollections.observableArrayList();

    public AdminDashboardController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.adminService = new AdminService();
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
                (SessionManager.getCurrentAdmin() != null ? SessionManager.getCurrentAdmin().getUsername() : "Admin") + "!");
        welcomeLabel.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-text-fill: #00ff88;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.4), 4,0,0,2);"
        );

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
        Tab dashboardTab       = createDashboardTab();
        Tab userApprovalsTab   = createUserApprovalsTab();
        Tab postsTab           = createPostsTab();
        Tab commentsTab        = createCommentsTab();
        Tab resolvedReportsTab = createResolvedReportsTab();  // ⬅️ NEW
        Tab inquiriesTab       = createInquiriesTab();
        Tab alertsTab          = createAlertsTab();
        Tab auditLogTab        = createAuditLogTab();

        tabPane.getTabs().addAll(
                dashboardTab,
                userApprovalsTab,
                postsTab,
                commentsTab,
                resolvedReportsTab,
                inquiriesTab,
                alertsTab,
                auditLogTab
        );

        // Apply tab styling
        for (Tab tab : tabPane.getTabs()) {
            styleTab(tab, tab.isSelected());
            tab.setOnSelectionChanged(e -> {
                styleTab(tab, tab.isSelected());

                // when the Dashboard tab is selected, refresh its stats
                if (tab.isSelected() && "Dashboard".equals(tab.getText())) {
                    refreshDashboardStats();
                }
            });
        }

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

        HBox logoutContainer = new HBox(logoutButton);
        logoutContainer.setAlignment(Pos.CENTER_RIGHT);
        logoutContainer.setPadding(new Insets(0, 20, 0, 0));

        mainContainer.getChildren().addAll(welcomeLabel, tabPane, logoutContainer);
        return mainContainer;
    }

    // ----- Dashboard tab (unchanged) -----

    private Tab createDashboardTab() {
        Tab tab = new Tab("Dashboard");
        tab.setClosable(false);

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        StyleUtil.applyDarkTheme(content);

        Label title = new Label("Admin Dashboard Overview");
        title.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(15);
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.setPadding(new Insets(20));

        AdminService.DashboardStats stats = adminService.getDashboardStats();

        // Row 1
        VBox totalUsersCard = createStatCard("Total Users", String.valueOf(stats.totalUsers), "#00ff88");
        VBox unapprovedUsersCard = createStatCard("Unapproved Users", String.valueOf(stats.unapprovedUsers), "#ff4444");
        VBox totalPostsCard = createStatCard("Total Posts", String.valueOf(stats.totalPosts), "#44aaff");

        // grab label references (2nd child = value label)
        totalUsersValueLabel       = (Label) totalUsersCard.getChildren().get(1);
        unapprovedUsersValueLabel  = (Label) unapprovedUsersCard.getChildren().get(1);
        totalPostsValueLabel       = (Label) totalPostsCard.getChildren().get(1);

        statsGrid.add(totalUsersCard,      0, 0);
        statsGrid.add(unapprovedUsersCard, 1, 0);
        statsGrid.add(totalPostsCard,      2, 0);

        // Row 2
        VBox reportedPostsCard = createStatCard("Reported Posts", String.valueOf(stats.reportedPosts), "#ffaa00");
        VBox reportedCommentsCard = createStatCard("Reported Comments", String.valueOf(stats.reportedComments), "#ffaa00");
        VBox pendingInquiriesCard = createStatCard("Pending Inquiries", String.valueOf(stats.pendingInquiries), "#aa44ff");

        reportedPostsValueLabel     = (Label) reportedPostsCard.getChildren().get(1);
        reportedCommentsValueLabel  = (Label) reportedCommentsCard.getChildren().get(1);
        pendingInquiriesValueLabel  = (Label) pendingInquiriesCard.getChildren().get(1);

        statsGrid.add(reportedPostsCard,    0, 1);
        statsGrid.add(reportedCommentsCard, 1, 1);
        statsGrid.add(pendingInquiriesCard, 2, 1);

        content.getChildren().addAll(title, statsGrid);
        tab.setContent(content);
        return tab;
    }

    private void refreshDashboardStats() {
        AdminService.DashboardStats stats = adminService.getDashboardStats();

        if (totalUsersValueLabel != null) {
            totalUsersValueLabel.setText(String.valueOf(stats.totalUsers));
        }
        if (unapprovedUsersValueLabel != null) {
            unapprovedUsersValueLabel.setText(String.valueOf(stats.unapprovedUsers));
        }
        if (totalPostsValueLabel != null) {
            totalPostsValueLabel.setText(String.valueOf(stats.totalPosts));
        }
        if (reportedPostsValueLabel != null) {
            reportedPostsValueLabel.setText(String.valueOf(stats.reportedPosts));
        }
        if (reportedCommentsValueLabel != null) {
            reportedCommentsValueLabel.setText(String.valueOf(stats.reportedComments));
        }
        if (pendingInquiriesValueLabel != null) {
            pendingInquiriesValueLabel.setText(String.valueOf(stats.pendingInquiries));
        }
    }



    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: #1a2d3f;" +
                        "-fx-padding: 20;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;"
        );
        card.setPrefSize(150, 100);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 24px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private Tab createUserApprovalsTab() {
        Tab tab = new Tab("User Approvals");
        tab.setClosable(false);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        StyleUtil.applyDarkTheme(content);

        UserManagementController userController = new UserManagementController();
        VBox userManagementContent = userController.createUserManagementTab();
        tab.setContent(userManagementContent);

        return tab;
    }

    private Tab createPostsTab() {
        Tab tab = new Tab("Posts");
        tab.setClosable(false);

        VBox content = new VBox();
        StyleUtil.applyDarkTheme(content);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));

        PostManagementController postController = new PostManagementController();
        VBox postManagementContent = postController.createPostManagementTab();
        tab.setContent(postManagementContent);

        return tab;
    }

    private Tab createCommentsTab() {
        Tab tab = new Tab("Comments");
        tab.setClosable(false);

        VBox content = new VBox();
        StyleUtil.applyDarkTheme(content);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));

        CommentManagementController commentController = new CommentManagementController();
        VBox commentManagementContent = commentController.createCommentManagementTab();
        tab.setContent(commentManagementContent);

        return tab;
    }

    // ---------- NEW: Resolved Reports tab ----------

    private Tab createResolvedReportsTab() {
        Tab tab = new Tab("Resolved");
        tab.setClosable(false);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        StyleUtil.applyDarkTheme(content);

        ResolvedReportsController controller = new ResolvedReportsController();
        VBox resolvedContent = controller.createResolvedReportsView();

        tab.setContent(resolvedContent);
        return tab;
    }

    private Tab createInquiriesTab() {
        Tab tab = new Tab("Inquiries");
        tab.setClosable(false);

        VBox content = new VBox();
        StyleUtil.applyDarkTheme(content);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));

        InquiryManagementController inquiryController = new InquiryManagementController();
        VBox inquiryManagementContent = inquiryController.createInquiryManagementTab();
        tab.setContent(inquiryManagementContent);

        return tab;
    }

    private Tab createAlertsTab() {
        Tab tab = new Tab("Alerts");
        tab.setClosable(false);

        VBox content = new VBox();
        StyleUtil.applyDarkTheme(content);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));

        AlertManagementController alertController = new AlertManagementController();
        VBox alertManagementContent = alertController.createAlertManagementTab();
        tab.setContent(alertManagementContent);

        return tab;
    }

    // ----- Tab styling -----

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

    // ----- Existing helper methods (unchanged) -----

    private void refreshUnapprovedUsers() {
        // unapprovedUsers.setAll(adminService.getUnapprovedUsers());
    }

    private void handleApprove(TableView<User> table) {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StyleUtil.showErrorAlert("No Selection", "Please select a user to approve.");
            return;
        }
        StyleUtil.showSuccessAlert("Success", selected.getUsername() + " has been approved (demo).");
        refreshUnapprovedUsers();
    }

    private void handleRemove(TableView<User> table) {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StyleUtil.showErrorAlert("No Selection", "Please select a user to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText("Remove user " + selected.getUsername() + "?");
        confirm.setContentText("This action cannot be undone.");

        confirm.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                StyleUtil.showSuccessAlert("Success", selected.getUsername() + " has been removed (demo).");
                refreshUnapprovedUsers();
            }
        });
    }

    private Tab createAuditLogTab() {
        Tab tab = new Tab("Audit Log");
        tab.setClosable(false);

        AuditLogController controller = new AuditLogController();
        VBox content = controller.createAuditLogTab();
        tab.setContent(content);

        return tab;
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
}
