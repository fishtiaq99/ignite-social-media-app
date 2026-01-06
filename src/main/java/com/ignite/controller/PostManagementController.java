package com.ignite.controller;

import com.ignite.model.Post;
import com.ignite.model.User;
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
import java.util.ArrayList;
import java.util.List;

public class PostManagementController {

    private final AdminService adminService;
    private VBox postsContainer;          // where all post cards go
    private Label totalPostsLabel;        // stats labels
    private Label reportedPostsLabel;
    private String currentFilter = "ALL"; // remember last filter used

    public PostManagementController() {
        this.adminService = new AdminService();
    }

    public VBox createPostManagementTab() {
        VBox mainContainer = new VBox(20);
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(20));

        // Title (same look as User Management)
        Label titleLabel = new Label("Post Management");
        titleLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox controlsRow = createControlsRow();   // filters + refresh in one line
        HBox contentRow  = createContentRow();    // posts on left, stats on right

        VBox.setVgrow(contentRow, Priority.ALWAYS);

        mainContainer.getChildren().addAll(
                titleLabel,
                controlsRow,
                contentRow
        );

        // initial load
        refreshStats();
        applyCurrentFilter();  // uses default currentFilter = "ALL"

        return mainContainer;
    }

    // ---------- TOP CONTROLS (FILTERS + REFRESH) ----------

    private HBox createControlsRow() {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(10, 0, 10, 0));

        Button allPostsBtn   = createFilterButton("All Posts", "ALL");
        Button reportedBtn   = createFilterButton("Reported Posts", "REPORTED");
        Button recentBtn     = createFilterButton("Recent", "RECENT");
        Button withMediaBtn  = createFilterButton("With Media", "MEDIA");

        Button refreshBtn = StyleUtil.createSecondaryButton("Refresh");
        refreshBtn.setOnAction(e -> {
            refreshStats();
            applyCurrentFilter();
        });

        row.getChildren().addAll(
                allPostsBtn,
                reportedBtn,
                recentBtn,
                withMediaBtn,
                refreshBtn
        );

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
            handleFilter(filterType);
        });
        return button;
    }

    // ---------- MIDDLE CONTENT ROW (POSTS + STATS ON THE SIDE) ----------

    private HBox createContentRow() {
        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_CENTER);
        row.setPadding(new Insets(10, 0, 0, 0));

        VBox postsSection = createPostsSection();     // big box on the left
        VBox statsColumn  = createStatsColumn();      // small column on the right

        HBox.setHgrow(postsSection, Priority.ALWAYS);

        row.getChildren().addAll(postsSection, statsColumn);
        return row;
    }

    // ---------- STATS COLUMN (RIGHT SIDE) ----------

    private VBox createStatsColumn() {
        VBox col = new VBox(10);
        col.setAlignment(Pos.TOP_CENTER);
        col.setPadding(new Insets(0, 10, 0, 0));

        VBox totalCard = createStatCard("Total Posts", "0", "#00ff88");
        VBox reportedCard = createStatCard("Reported Posts", "0", "#ff4444");

        // labels are the second child (index 1) in each card
        totalPostsLabel = (Label) totalCard.getChildren().get(1);
        reportedPostsLabel = (Label) reportedCard.getChildren().get(1);

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
        int totalPosts = adminService.getTotalPostsCount();
        int reportedPosts = adminService.getReportedPostsCount();

        if (totalPostsLabel != null) {
            totalPostsLabel.setText(String.valueOf(totalPosts));
        }
        if (reportedPostsLabel != null) {
            reportedPostsLabel.setText(String.valueOf(reportedPosts));
        }
    }

    // ---------- POSTS SECTION (LEFT SIDE, BIG BOX) ----------

    private VBox createPostsSection() {
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

        Label header = new Label("Posts");
        header.setStyle(
                "-fx-text-fill: #00ff88;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 0 0 8 0;"
        );

        postsContainer = new VBox(15);
        postsContainer.setPadding(new Insets(10));
        postsContainer.setPrefHeight(500); // plenty of vertical space

        ScrollPane scrollPane = new ScrollPane(postsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefViewportHeight(500);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        container.getChildren().addAll(header, scrollPane);
        return container;
    }

    private void renderPosts(List<Post> posts, boolean highlightAsReported) {
        postsContainer.getChildren().clear();

        if (posts == null || posts.isEmpty()) {
            Label placeholder = new Label("No posts to display.");
            placeholder.setStyle("-fx-text-fill: #cccccc;");
            postsContainer.getChildren().add(placeholder);
            return;
        }

        for (Post post : posts) {
            VBox card = createPostCard(post, highlightAsReported);
            card.setMaxWidth(Double.MAX_VALUE);
            postsContainer.getChildren().add(card);
        }
    }

    private VBox createPostCard(Post post, boolean highlightAsReported) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-background-color: #1a1a2e;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + (highlightAsReported ? "#ff4444" : "#00ff88") + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        // Author
        User author = post.getAuthor();
        String authorName = (author != null ? author.getUsername() : "Unknown");

        Label authorLabel = new Label("Posted by: " + authorName);
        authorLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        // Date
        String dateText;
        if (post.getCreationDate() != null) {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            dateText = df.format(post.getCreationDate());
        } else {
            dateText = "Unknown date";
        }
        Label dateLabel = new Label("Posted on: " + dateText);
        dateLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        // Content (preview)
        String text = post.getContentText();
        if (text == null || text.isEmpty()) {
            text = "(No content)";
        }
        text = text.replace("\n", " ");
        String preview = text.length() > 200 ? text.substring(0, 197) + "..." : text;

        Label contentLabel = new Label(preview);
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: white;");

        // Media indicator
        Label mediaLabel = null;
        if (post.getMediaUrl() != null && !post.getMediaUrl().isEmpty()) {
            mediaLabel = new Label("Media attached");
            mediaLabel.setStyle("-fx-text-fill: #44aaff; -fx-font-size: 12px;");
        }

        // Reported badge
        Label statusBadge = null;
        if (highlightAsReported) {
            statusBadge = new Label("REPORTED");
            statusBadge.setStyle(
                    "-fx-text-fill: #ff4444; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 11px;"
            );
        }

        // Buttons: View details + Delete
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button viewBtn = StyleUtil.createSecondaryButton("View Details");
        Button deleteBtn = new Button("Delete Post");
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

        viewBtn.setOnAction(e -> showPostDetails(post));
        deleteBtn.setOnAction(e -> handleDeletePost(post));

        buttonRow.getChildren().addAll(viewBtn, deleteBtn);

        card.getChildren().addAll(authorLabel, dateLabel, contentLabel);
        if (mediaLabel != null) card.getChildren().add(mediaLabel);
        if (statusBadge != null) card.getChildren().add(statusBadge);
        card.getChildren().add(buttonRow);

        return card;
    }

    // ---------- DATA LOADING & FILTERS ----------

    private void loadAllPosts() {
        List<Post> posts = adminService.getAllPostsWithAuthors();
        renderPosts(posts, false);
    }

    private void handleFilter(String filterType) {
        switch (filterType) {
            case "ALL" -> loadAllPosts();

            case "REPORTED" -> {
                List<Post> reported = adminService.getReportedPostsWithAuthors();
                renderPosts(reported, true);
            }

            case "RECENT" -> {
                List<Post> all = adminService.getAllPostsWithAuthors();
                List<Post> recent = new ArrayList<>();
                int max = Math.min(50, all.size());
                for (int i = 0; i < max; i++) {
                    recent.add(all.get(i));
                }
                renderPosts(recent, false);
            }

            case "MEDIA" -> {
                List<Post> all = adminService.getAllPostsWithAuthors();
                List<Post> withMedia = new ArrayList<>();
                for (Post p : all) {
                    if (p.getMediaUrl() != null && !p.getMediaUrl().isEmpty()) {
                        withMedia.add(p);
                    }
                }
                renderPosts(withMedia, false);
            }
        }
    }

    private void applyCurrentFilter() {
        handleFilter(currentFilter);
    }

    // ---------- ACTION HANDLERS ----------

    private void showPostDetails(Post post) {
        Alert details = new Alert(Alert.AlertType.INFORMATION);
        details.setTitle("Post Details");
        details.setHeaderText("Post ID: " + post.getPostId());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a2d3f;");

        Label authorLabel = new Label("Author: " +
                (post.getAuthor() != null ? post.getAuthor().getUsername() : "Unknown"));
        authorLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        Label dateLabel = new Label("Created: " + post.getCreationDate());
        dateLabel.setStyle("-fx-text-fill: #cccccc;");

        Label mediaLabel = new Label("Media URL: " +
                (post.getMediaUrl() != null && !post.getMediaUrl().isEmpty()
                        ? post.getMediaUrl()
                        : "(none)"));
        mediaLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");

        Label contentLabel = new Label("Content:");
        contentLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        TextArea contentText = new TextArea(post.getContentText());
        contentText.setEditable(false);
        contentText.setWrapText(true);
        contentText.setStyle(
                "-fx-control-inner-background: #0d1b2a; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #00ff88;"
        );

        content.getChildren().addAll(authorLabel, dateLabel, mediaLabel, contentLabel, contentText);

        details.getDialogPane().setContent(content);
        details.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        details.showAndWait();
    }

    private void handleDeletePost(Post post) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete this post?");
        confirm.setContentText("This action cannot be undone.");

        confirm.getDialogPane().setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-border-color: #ff4444; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (adminService.deletePost(post.getPostId())) {
                    StyleUtil.showSuccessAlert("Success", "Post has been deleted successfully.");
                    refreshStats();
                    applyCurrentFilter();
                } else {
                    StyleUtil.showErrorAlert("Error", "Failed to delete post.");
                }
            }
        });
    }
}
