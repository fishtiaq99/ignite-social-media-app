package com.ignite.controller;

import com.ignite.service.ReportService;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class ReportsTabController {

    private final ReportService reportService;

    public ReportsTabController() {
        this.reportService = new ReportService();
    }

    public Tab createTab() {
        Tab reportsTab = new Tab("Reports");
        reportsTab.setClosable(false);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: #0d1b2a;");

        // --- Title (plain neon green) ---
        Label titleLabel = new Label("Submit a Report");
        titleLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 26px; -fx-font-weight: bold;");
        content.getChildren().add(titleLabel);

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER_LEFT);

        // --- Post ID field ---
        var postIdField = StyleUtil.createStyledTextField("Post ID (optional)");
        // --- Comment ID field ---
        var commentIdField = StyleUtil.createStyledTextField("Comment ID (optional)");

        // --- Reason TextArea ---
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Explain why you are reporting this content...");
        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(5);
        reasonArea.setStyle(
                "-fx-control-inner-background: #0d1b2a; " + // navy blue input
                        "-fx-background-color: #0d1b2a; " +          // outer background
                        "-fx-text-fill: white; " +                   // text color
                        "-fx-border-color: #404040; " +             // border
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 10;"
        );

        // Focus glow effect
        reasonArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                reasonArea.setStyle(
                        "-fx-control-inner-background: #0d1b2a; " +
                                "-fx-background-color: #0d1b2a; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #00ff88; " +
                                "-fx-border-radius: 5; " +
                                "-fx-padding: 10;"
                );
            } else {
                reasonArea.setStyle(
                        "-fx-control-inner-background: #0d1b2a; " +
                                "-fx-background-color: #0d1b2a; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #404040; " +
                                "-fx-border-radius: 5; " +
                                "-fx-padding: 10;"
                );
            }
        });

        // --- Submit Button ---
        var reportButton = StyleUtil.createPrimaryButton("Submit Report");
        reportButton.setOnAction(e -> {
            try {
                Integer postId = parseInteger(postIdField.getText());
                Integer commentId = parseInteger(commentIdField.getText());
                String reason = reasonArea.getText().trim();

                if ((postId == null && commentId == null) || (postId != null && commentId != null)) {
                    StyleUtil.showErrorAlert("Validation Error",
                            "Provide either a Post ID or a Comment ID, not both.");
                    return;
                }

                boolean success = reportService.submitReport(
                        SessionManager.getCurrentUserId(), postId, commentId, reason
                );

                if (success) {
                    StyleUtil.showSuccessAlert("Success", "Report submitted successfully!");
                    postIdField.clear();
                    commentIdField.clear();
                    reasonArea.clear();
                } else {
                    StyleUtil.showErrorAlert("Error", "Failed to submit report.");
                }

            } catch (IllegalArgumentException ex) {
                StyleUtil.showErrorAlert("Error", ex.getMessage());
            }
        });

        formBox.getChildren().addAll(postIdField, commentIdField, reasonArea, reportButton);
        content.getChildren().add(formBox);

        reportsTab.setContent(content);
        return reportsTab;
    }

    private Integer parseInteger(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID must be a valid integer");
        }
    }
}
