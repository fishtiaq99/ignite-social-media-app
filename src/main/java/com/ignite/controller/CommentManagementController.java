package com.ignite.controller;

import com.ignite.model.Comment;
import com.ignite.service.AdminService;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentManagementController {

    private final AdminService adminService;

    private VBox commentsContainer;          // where comment cards go
    private Label totalCommentsLabel;        // stats labels
    private Label reportedCommentsLabel;
    private String currentFilter = "ALL";    // remember last filter

    public CommentManagementController() {
        this.adminService = new AdminService();
    }

    public VBox createCommentManagementTab() {
        VBox mainContainer = new VBox(20);
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(20));

        Label titleLabel = new Label("Comment Management");
        titleLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox controlsRow = createControlsRow();   // filters + refresh
        HBox contentRow  = createContentRow();    // comments on left, stats on right

        VBox.setVgrow(contentRow, Priority.ALWAYS);

        mainContainer.getChildren().addAll(
                titleLabel,
                controlsRow,
                contentRow
        );

        // one central call that refreshes stats + applies current filter
        applyCurrentFilter(); // initial load

        return mainContainer;
    }

    // ---------- TOP CONTROLS (FILTERS + REFRESH) ----------

    private HBox createControlsRow() {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(10, 0, 10, 0));

        Button allBtn      = createFilterButton("All Comments", "ALL");
        Button reportedBtn = createFilterButton("Reported", "REPORTED");
        Button recentBtn   = createFilterButton("Recent", "RECENT");

        Button refreshBtn = StyleUtil.createSecondaryButton("Refresh");
        refreshBtn.setOnAction(e -> applyCurrentFilter());

        row.getChildren().addAll(allBtn, reportedBtn, recentBtn, refreshBtn);
        return row;
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
            applyCurrentFilter(); // filter + stats refresh together
        });

        return button;
    }

    // ---------- MIDDLE CONTENT ROW (COMMENTS + STATS) ----------

    private HBox createContentRow() {
        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_CENTER);
        row.setPadding(new Insets(10, 0, 0, 0));

        VBox commentsSection = createCommentsSection(); // big box on left
        VBox statsColumn     = createStatsColumn();     // small column on right

        HBox.setHgrow(commentsSection, Priority.ALWAYS);

        row.getChildren().addAll(commentsSection, statsColumn);
        return row;
    }

    // ---------- STATS COLUMN (RIGHT SIDE) ----------

    private VBox createStatsColumn() {
        VBox col = new VBox(10);
        col.setAlignment(Pos.TOP_CENTER);
        col.setPadding(new Insets(0, 10, 0, 0));

        VBox totalCard = createStatCard("Total Comments", "0", "#00ff88");
        VBox reportedCard = createStatCard("Reported Comments", "0", "#ff4444");

        totalCommentsLabel = (Label) totalCard.getChildren().get(1);
        reportedCommentsLabel = (Label) reportedCard.getChildren().get(1);

        col.getChildren().addAll(totalCard, reportedCard);
        return col;
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
                        "-fx-border-radius: 10;"
        );
        card.setPrefSize(130, 70);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private void refreshStats() {
        int totalComments = adminService.getTotalCommentsCount();
        int reportedComments = adminService.getReportedCommentsCount();

        if (totalCommentsLabel != null) {
            totalCommentsLabel.setText(String.valueOf(totalComments));
        }
        if (reportedCommentsLabel != null) {
            reportedCommentsLabel.setText(String.valueOf(reportedComments));
        }
    }

    // ---------- COMMENTS SECTION (LEFT SIDE, BIG BOX) ----------

    private VBox createCommentsSection() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxWidth(900);
        container.setPrefWidth(800);
        container.setPadding(new Insets(15));
        container.setStyle(
                "-fx-background-color: #0b1723;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.25), 12,0,0,2);"
        );

        Label header = new Label("Comments");
        header.setStyle(
                "-fx-text-fill: #00ff88;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 0 0 8 0;"
        );

        commentsContainer = new VBox(15);
        commentsContainer.setPadding(new Insets(10));
        commentsContainer.setPrefHeight(500);

        ScrollPane scrollPane = new ScrollPane(commentsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefViewportHeight(500);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        container.getChildren().addAll(header, scrollPane);
        return container;
    }

    private void renderComments(List<Comment> comments, boolean highlightReported) {
        commentsContainer.getChildren().clear();

        if (comments == null || comments.isEmpty()) {
            Label placeholder = new Label("No comments to display.");
            placeholder.setStyle("-fx-text-fill: #cccccc;");
            commentsContainer.getChildren().add(placeholder);
            return;
        }

        for (Comment c : comments) {
            VBox card = createCommentCard(c, highlightReported);
            card.setMaxWidth(Double.MAX_VALUE);
            commentsContainer.getChildren().add(card);
        }
    }

    private VBox createCommentCard(Comment c, boolean highlightReported) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-background-color: #1a1a2e;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + (highlightReported ? "#ff4444" : "#00ff88") + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        Label metaLine = new Label(
                "Comment #" + c.getCommentId() +
                        "  |  User ID: " + c.getUserId() +
                        "  |  Post ID: " + c.getPostId()
        );
        metaLine.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 12px;");

        String dateText;
        if (c.getCreationDate() != null) {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            dateText = df.format(c.getCreationDate());
        } else {
            dateText = "Unknown date";
        }
        Label dateLabel = new Label("Posted on: " + dateText);
        dateLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 11px;");

        String text = c.getContentText();
        if (text == null || text.isEmpty()) {
            text = "(No content)";
        }
        text = text.replace("\n", " ");
        String preview = text.length() > 200 ? text.substring(0, 197) + "..." : text;

        Label contentLabel = new Label(preview);
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: white;");

        Label badge = null;
        if (highlightReported) {
            badge = new Label("REPORTED");
            badge.setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold; -fx-font-size: 11px;");
        }

        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button viewBtn = StyleUtil.createSecondaryButton("View Details");
        Button deleteBtn = new Button("Delete Comment");
        deleteBtn.setStyle(
                "-fx-background-color: #ff4444;" +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 6 12; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );

        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(
                "-fx-background-color: #ff6666;" +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 6 12; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow( gaussian , rgba(255,68,68,0.6) , 8,0,0,0 );"
        ));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(
                "-fx-background-color: #ff4444;" +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 6 12; " +
                        "-fx-background-radius: 8;"
        ));

        viewBtn.setOnAction(e -> showCommentDetails(c));
        deleteBtn.setOnAction(e -> handleDeleteComment(c));

        buttonRow.getChildren().addAll(viewBtn, deleteBtn);

        card.getChildren().addAll(metaLine, dateLabel, contentLabel);
        if (badge != null) card.getChildren().add(badge);
        card.getChildren().add(buttonRow);

        return card;
    }

    // ---------- DATA & FILTERS ----------

    private void handleFilter(String filterType) {
        switch (filterType) {
            case "ALL" -> {
                List<Comment> all = adminService.getAllComments();
                renderComments(all, false);
            }
            case "REPORTED" -> {
                List<Comment> reported = adminService.getReportedComments();
                renderComments(reported, true);
            }
            case "RECENT" -> {
                List<Comment> recent = adminService.getRecentComments(50);
                renderComments(recent, false);
            }
        }
    }

    /**
     * Central helper: refreshes stats and then reapplies the current filter.
     * Call this whenever data might have changed or when user hits Refresh.
     */
    private void applyCurrentFilter() {
        refreshStats();
        handleFilter(currentFilter);
    }

    // ---------- ACTION HANDLERS ----------

    private void showCommentDetails(Comment c) {
        Alert details = new Alert(Alert.AlertType.INFORMATION);
        details.setTitle("Comment Details");
        details.setHeaderText("Comment ID: " + c.getCommentId());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a2d3f;");

        Label idsLabel = new Label(
                "User ID: " + c.getUserId() + "   |   Post ID: " + c.getPostId()
        );
        idsLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        Label dateLabel = new Label("Created: " + c.getCreationDate());
        dateLabel.setStyle("-fx-text-fill: #cccccc;");

        Label contentLabel = new Label("Content:");
        contentLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        TextArea contentText = new TextArea(c.getContentText());
        contentText.setEditable(false);
        contentText.setWrapText(true);
        contentText.setStyle(
                "-fx-control-inner-background: #0d1b2a; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00ff88;"
        );

        content.getChildren().addAll(idsLabel, dateLabel, contentLabel, contentText);

        details.getDialogPane().setContent(content);
        details.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        details.showAndWait();
    }

    private void handleDeleteComment(Comment c) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete this comment?");
        confirm.setContentText("This action cannot be undone.");

        confirm.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #ff4444; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (adminService.deleteComment(c.getCommentId())) {
                    StyleUtil.showSuccessAlert("Success", "Comment has been deleted.");
                    applyCurrentFilter(); // stats + list refreshed together
                } else {
                    StyleUtil.showErrorAlert("Error", "Failed to delete comment.");
                }
            }
        });
    }
}
