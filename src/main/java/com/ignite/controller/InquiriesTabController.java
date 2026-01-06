package com.ignite.controller;

import com.ignite.model.Inquiry;
import com.ignite.service.InquiryService;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class InquiriesTabController {

    private InquiryService inquiryService;
    private VBox inquiriesContainer;
    private TextArea newInquiryArea;

    public InquiriesTabController() {
        this.inquiryService = new InquiryService();
    }

    public Tab createTab() {
        Tab inquiriesTab = new Tab("Inquiries");
        inquiriesTab.setClosable(false);

        VBox mainContainer = new VBox();
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(20));

        // Title
        Label titleLabel = StyleUtil.createTitleLabel("Help & Support");

        // Create two main sections: New Inquiry and Inquiry History
        VBox contentContainer = new VBox(30);
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setMaxWidth(800);

        // Section 1: New Inquiry Form
        VBox newInquirySection = createNewInquirySection();

        // Section 2: Inquiry History
        VBox inquiryHistorySection = createInquiryHistorySection();

        contentContainer.getChildren().addAll(newInquirySection, inquiryHistorySection);
        mainContainer.getChildren().addAll(titleLabel, contentContainer);

        // Load initial inquiries
        loadInquiries("ALL");

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0d1b2a; -fx-background-color: transparent;");

        inquiriesTab.setContent(scrollPane);

        return inquiriesTab;
    }

    private VBox createNewInquirySection() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.TOP_CENTER);
        section.setMaxWidth(600);

        Label sectionLabel = new Label("Send New Inquiry");
        sectionLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label instructionLabel = new Label("Have a question or need help? Send a message to our admin team.");
        instructionLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        newInquiryArea = new TextArea();
        newInquiryArea.setPromptText("Describe your issue or question here...");
        newInquiryArea.setWrapText(true);
        newInquiryArea.setStyle(
                "-fx-background-color: #001f3f;" +               // Outer background (navy)
                        "-fx-control-inner-background: #001f3f;" +       // Inner typing area (navy)
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 12 15;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #00ff88;" +                   // Neon border
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;"
        );

        newInquiryArea.setPrefRowCount(4);
        newInquiryArea.setMaxWidth(500);

        // Character counter
        Label charCountLabel = new Label("0/1000");
        charCountLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        newInquiryArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int length = newValue.length();
            charCountLabel.setText(length + "/1000");
            if (length > 1000) {
                charCountLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px;");
            } else {
                charCountLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
            }
        });

        Button submitButton = StyleUtil.createPrimaryButton("Submit Inquiry");
        submitButton.setOnAction(e -> submitInquiry());

        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.getChildren().addAll(newInquiryArea, charCountLabel, submitButton);

        section.getChildren().addAll(sectionLabel, instructionLabel, formContainer);
        return section;
    }

    private VBox createInquiryHistorySection() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.TOP_CENTER);
        section.setMaxWidth(700);

        Label sectionLabel = new Label("Your Inquiry History");
        sectionLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Filter buttons
        HBox filterContainer = createFilterButtons();

        // Inquiries container
        inquiriesContainer = new VBox(15);
        inquiriesContainer.setAlignment(Pos.TOP_CENTER);
        inquiriesContainer.setMaxWidth(650);
        inquiriesContainer.setPadding(new Insets(10));

        section.getChildren().addAll(sectionLabel, filterContainer, inquiriesContainer);
        return section;
    }

    private HBox createFilterButtons() {
        HBox filterContainer = new HBox(10);
        filterContainer.setAlignment(Pos.CENTER);

        Button allButton = createHistoryFilterButton("All Inquiries", "ALL");
        Button pendingButton = createHistoryFilterButton("Pending", "PENDING");
        Button resolvedButton = createHistoryFilterButton("Resolved", "RESOLVED");

        filterContainer.getChildren().addAll(allButton, pendingButton, resolvedButton);
        return filterContainer;
    }

    private Button createHistoryFilterButton(String text, String filterType) {
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

        button.setOnAction(e -> loadInquiries(filterType));
        return button;
    }

    private void submitInquiry() {
        String message = newInquiryArea.getText().trim();
        int currentUserId = SessionManager.getCurrentUserId();

        if (message.isEmpty()) {
            StyleUtil.showErrorAlert("Error", "Please enter your inquiry message");
            return;
        }

        if (message.length() > 1000) {
            StyleUtil.showErrorAlert("Error", "Inquiry message cannot exceed 1000 characters");
            return;
        }

        try {
            boolean success = inquiryService.submitInquiry(currentUserId, message);
            if (success) {
                StyleUtil.showSuccessAlert("Success", "Your inquiry has been submitted successfully!");
                newInquiryArea.clear();
                loadInquiries("ALL"); // Refresh the list
            } else {
                StyleUtil.showErrorAlert("Error", "Failed to submit inquiry. Please try again.");
            }
        } catch (Exception e) {
            StyleUtil.showErrorAlert("Error", e.getMessage());
        }
    }

    private void loadInquiries(String filterType) {
        inquiriesContainer.getChildren().clear();

        int currentUserId = SessionManager.getCurrentUserId();
        List<Inquiry> inquiries;

        switch (filterType) {
            case "PENDING":
                inquiries = inquiryService.getUserInquiriesByStatus(currentUserId, false);
                break;
            case "RESOLVED":
                inquiries = inquiryService.getUserInquiriesByStatus(currentUserId, true);
                break;
            default:
                inquiries = inquiryService.getUserInquiries(currentUserId);
                break;
        }

        if (inquiries.isEmpty()) {
            Label noInquiriesLabel = new Label("No " + (filterType.equals("ALL") ? "" : filterType.toLowerCase()) + " inquiries found");
            noInquiriesLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 16px; -fx-padding: 40;");
            inquiriesContainer.getChildren().add(noInquiriesLabel);
            return;
        }

        for (Inquiry inquiry : inquiries) {
            inquiriesContainer.getChildren().add(createInquiryCard(inquiry));
        }
    }

    private VBox createInquiryCard(Inquiry inquiry) {
        VBox inquiryCard = new VBox(12);
        inquiryCard.setStyle(
                "-fx-background-color: #1a2d3f;" +
                        "-fx-padding: 20;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + (inquiry.isResolved() ? "#00ff88" : "#ffaa00") + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;"
        );
        inquiryCard.setMaxWidth(600);

        // Header with status and date
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setSpacing(10);

        // Status badge
        Label statusLabel = new Label(inquiry.isResolved() ? "RESOLVED" : "PENDING");
        statusLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 3 8; " +
                        "-fx-background-color: " + (inquiry.isResolved() ? "#00aa44" : "#ffaa00") + ";" +
                        "-fx-background-radius: 8;"
        );

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
        Label dateLabel = new Label(dateFormat.format(inquiry.getSubmitDate()));
        dateLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 12px;");

        HBox.setHgrow(dateLabel, Priority.ALWAYS);
        headerRow.getChildren().addAll(statusLabel, dateLabel);

        // Inquiry message
        Label messageLabel = new Label(inquiry.getMessage());
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);

        // Admin response section (if resolved)
        if (inquiry.isResolved()) {
            VBox responseSection = createResponseSection(inquiry);
            inquiryCard.getChildren().addAll(headerRow, messageLabel, responseSection);
        } else {
            Label waitingLabel = new Label("⏳ Waiting for admin response...");
            waitingLabel.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 12px; -fx-font-style: italic;");
            inquiryCard.getChildren().addAll(headerRow, messageLabel, waitingLabel);
        }

        return inquiryCard;
    }

    private VBox createResponseSection(Inquiry inquiry) {
        VBox responseSection = new VBox(8);
        responseSection.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 12; -fx-background-radius: 8;");

        Label responseTitle = new Label("📨 Admin Response:");
        responseTitle.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 13px; -fx-font-weight: bold;");

        // TODO: Replace with actual admin response from database
        // For now, using a placeholder
        Label responseLabel = new Label("Thank you for your inquiry. Our admin team has reviewed your request and will get back to you shortly with a detailed response.");
        responseLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px;");
        responseLabel.setWrapText(true);

        responseSection.getChildren().addAll(responseTitle, responseLabel);
        return responseSection;
    }
}