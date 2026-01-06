package com.ignite.controller;

import com.ignite.model.Inquiry;
import com.ignite.model.enums.InquiryStatus;
import com.ignite.service.AdminService;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.List;

public class InquiryManagementController {

    private final AdminService adminService;

    private VBox inquiriesContainer;
    private Label totalInquiriesLabel;
    private Label unansweredInquiriesLabel;
    private String currentFilter = "ALL";

    public InquiryManagementController() {
        this.adminService = new AdminService();
    }

    public VBox createInquiryManagementTab() {
        VBox mainContainer = new VBox(10);
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(20));

        Label title = new Label("Inquiry Management");
        title.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox topRow = createFiltersStatsAndRefresh();
        VBox inquiriesSection = createInquiriesSection();

        VBox.setVgrow(inquiriesSection, Priority.ALWAYS);

        mainContainer.getChildren().addAll(
                title,
                topRow,
                inquiriesSection
        );

        refreshStats();
        applyCurrentFilter();

        return mainContainer;
    }

    // ---------- TOP ROW: FILTERS + STATS + REFRESH ----------

    private HBox createFiltersStatsAndRefresh() {
        HBox topRow = new HBox(40);
        topRow.setAlignment(Pos.CENTER);
        topRow.setPadding(new Insets(10, 0, 20, 0));

        // Filters + Refresh
        VBox left = new VBox(10);
        left.setAlignment(Pos.CENTER);

        HBox filterRow = new HBox(10);
        filterRow.setAlignment(Pos.CENTER);

        Button allBtn      = createFilterButton("All", "ALL");
        Button pendingBtn  = createFilterButton("Pending", "PENDING");
        Button resolvedBtn = createFilterButton("Resolved", "RESOLVED");

        filterRow.getChildren().addAll(allBtn, pendingBtn, resolvedBtn);

        Button refreshBtn = StyleUtil.createSecondaryButton("Refresh");
        refreshBtn.setOnAction(e -> {
            refreshStats();
            applyCurrentFilter();
        });

        left.getChildren().addAll(filterRow, refreshBtn);

        // Stats on the right
        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER);

        VBox totalCard = createStatCard("Total Inquiries", "0", "#44aaff");
        VBox unansweredCard = createStatCard("Unanswered", "0", "#ffaa00");

        totalInquiriesLabel = (Label) totalCard.getChildren().get(1);
        unansweredInquiriesLabel = (Label) unansweredCard.getChildren().get(1);

        statsRow.getChildren().addAll(totalCard, unansweredCard);

        topRow.getChildren().addAll(left, statsRow);
        return topRow;
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

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #00ff88; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #1a2d3f; " +
                        "-fx-text-fill: #00ff88; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;"
        ));

        button.setOnAction(e -> {
            currentFilter = filterType;
            handleFilter(filterType);
        });

        return button;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: #1a2d3f;" +
                        "-fx-padding: 12;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;"
        );
        card.setPrefSize(150, 70);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private void refreshStats() {
        int total = adminService.getTotalInquiriesCount();
        int unanswered = adminService.getUnansweredInquiriesCount();

        if (totalInquiriesLabel != null) {
            totalInquiriesLabel.setText(String.valueOf(total));
        }
        if (unansweredInquiriesLabel != null) {
            unansweredInquiriesLabel.setText(String.valueOf(unanswered));
        }
    }

    // ---------- INQUIRIES SECTION ----------

    private VBox createInquiriesSection() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxWidth(1000);
        container.setStyle("-fx-padding: 5 20 5 20; -fx-background-color: transparent;");

        Label header = new Label("Inquiries");
        header.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 18px; -fx-font-weight: bold;");

        inquiriesContainer = new VBox(15);
        inquiriesContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(inquiriesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefViewportHeight(600);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        container.getChildren().addAll(header, scrollPane);
        return container;
    }

    private void renderInquiries(List<Inquiry> inquiries) {
        inquiriesContainer.getChildren().clear();

        if (inquiries == null || inquiries.isEmpty()) {
            Label placeholder = new Label("No inquiries to display.");
            placeholder.setStyle("-fx-text-fill: #cccccc;");
            inquiriesContainer.getChildren().add(placeholder);
            return;
        }

        for (Inquiry inq : inquiries) {
            VBox card = createInquiryCard(inq);
            inquiriesContainer.getChildren().add(card);
        }
    }

    private VBox createInquiryCard(Inquiry inq) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-background-color: #1a1a2e;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        // Top meta line
        Label meta = new Label(
                "Inquiry #" + inq.getInquiryId() +
                        "  |  User ID: " + inq.getUserId()
        );
        meta.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 12px;");

        // Date
        String dateText;
        if (inq.getSubmitDate() != null) {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            dateText = df.format(inq.getSubmitDate());
        } else {
            dateText = "Unknown date";
        }
        Label dateLabel = new Label("Submitted: " + dateText);
        dateLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 11px;");

        // Status label with color
        Label statusLabel = new Label("Status: " + inq.getStatusDisplay());
        String statusColor = inq.getStatus() == InquiryStatus.RESOLVED ? "#00ff88" : "#ffaa00";
        statusLabel.setStyle(
                "-fx-text-fill: " + statusColor + ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
        );

        // Message preview
        String text = inq.getMessage();
        if (text == null || text.isEmpty()) {
            text = "(No message)";
        }
        text = text.replace("\n", " ");
        String preview = text.length() > 200 ? text.substring(0, 197) + "..." : text;

        Label messageLabel = new Label(preview);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: white;");

        // Buttons
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = StyleUtil.createSecondaryButton(
                inq.getStatus() == InquiryStatus.RESOLVED ? "View" : "View / Answer"
        );

        viewButton.setOnAction(e -> showInquiryDialog(inq));

        buttonRow.getChildren().add(viewButton);

        card.getChildren().addAll(meta, dateLabel, statusLabel, messageLabel, buttonRow);
        return card;
    }

    // ---------- FILTER HANDLING ----------

    private void handleFilter(String filterType) {
        switch (filterType) {
            case "ALL" -> renderInquiries(adminService.getAllInquiries());
            case "PENDING" -> renderInquiries(adminService.getPendingInquiries());
            case "RESOLVED" -> renderInquiries(adminService.getResolvedInquiries());
        }
    }

    private void applyCurrentFilter() {
        handleFilter(currentFilter);
    }

    // ---------- DIALOG TO VIEW / ANSWER ----------

    private void showInquiryDialog(Inquiry inq) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Inquiry Details");
        dialog.setHeaderText("Inquiry ID: " + inq.getInquiryId());

        ButtonType closeBtnType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType answerBtnType = null;

        boolean canAnswer = inq.getStatus() == InquiryStatus.PENDING;

        if (canAnswer) {
            answerBtnType = new ButtonType("Send Response", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(answerBtnType, closeBtnType);
        } else {
            dialog.getDialogPane().getButtonTypes().addAll(closeBtnType);
        }

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #1a2d3f;");

        Label userLabel = new Label("User ID: " + inq.getUserId());
        userLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        Label dateLabel = new Label("Submitted: " + inq.getSubmitDate());
        dateLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");

        Label messageLabel = new Label("Message:");
        messageLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        TextArea messageArea = new TextArea(inq.getMessage());
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(6);
        messageArea.setStyle(
                "-fx-control-inner-background: #0d1b2a; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00ff88;"
        );

        content.getChildren().addAll(userLabel, dateLabel, messageLabel, messageArea);

        TextArea responseArea = null;

        if (canAnswer) {
            Label responseLabel = new Label("Your Response:");
            responseLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

            responseArea = new TextArea();
            responseArea.setWrapText(true);
            responseArea.setPrefRowCount(4);
            responseArea.setStyle(
                    "-fx-control-inner-background: #0d1b2a; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-color: #00ff88;"
            );

            content.getChildren().addAll(responseLabel, responseArea);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        TextArea finalResponseArea = responseArea;
        ButtonType finalAnswerBtnType = answerBtnType;

        dialog.setResultConverter(buttonType -> {
            if (finalAnswerBtnType != null && buttonType == finalAnswerBtnType) {
                return finalResponseArea.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(responseText -> {
            if (responseText != null && !responseText.trim().isEmpty()) {
                // Get current admin ID (adjust getAdminId() if your Admin model uses a different method)
                int adminId = 0;
                if (SessionManager.getCurrentAdmin() != null) {
                    adminId = SessionManager.getCurrentAdmin().getAdminId();
                }

                boolean ok = adminService.answerInquiry(inq.getInquiryId(), adminId, responseText);
                if (ok) {
                    StyleUtil.showSuccessAlert("Success", "Response sent and inquiry marked as resolved.");
                    refreshStats();
                    applyCurrentFilter();
                } else {
                    StyleUtil.showErrorAlert("Error", "Failed to send response.");
                }
            }
        });
    }
}
