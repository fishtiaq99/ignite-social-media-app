package com.ignite.controller;

import com.ignite.model.Post;
import com.ignite.service.PostService;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CreatePostTabController {

    private PostService postService;

    public CreatePostTabController() {
        this.postService = new PostService();
    }

    public Tab createTab() {
        Tab tab = new Tab("Create Post");
        tab.setClosable(false);

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: rgba(13, 27, 42, 0.8);");
        content.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Create New Post");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #00ff88; -fx-font-weight: bold;");

        // Post content
        Label contentLabel = new Label("Post Content:");
        contentLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        TextArea contentTextArea = new TextArea();
        contentTextArea.setPromptText("What's on your mind? (Use # for hashtags)");
        contentTextArea.setWrapText(true);
        contentTextArea.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-control-inner-background: #0d1b2a; -fx-border-color: #404040;");
        contentTextArea.setPrefHeight(150);
        contentTextArea.setMaxWidth(600);

        // Media URL (optional)
        Label mediaLabel = new Label("Media URL (optional):");
        mediaLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        TextField mediaField = StyleUtil.createStyledTextField("https://example.com/image.jpg");

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button postButton = StyleUtil.createPrimaryButton("Create Post");
        Button clearButton = StyleUtil.createSecondaryButton("Clear");

        postButton.setOnAction(e -> handleCreatePost(contentTextArea.getText(), mediaField.getText()));
        clearButton.setOnAction(e -> {
            contentTextArea.clear();
            mediaField.clear();
        });

        buttonBox.getChildren().addAll(postButton, clearButton);

        content.getChildren().addAll(titleLabel, contentLabel, contentTextArea, mediaLabel, mediaField, buttonBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);

        return tab;
    }

    private void handleCreatePost(String content, String mediaUrl) {
        try {
            if (content == null || content.trim().isEmpty()) {
                StyleUtil.showErrorAlert("Error", "Post content cannot be empty");
                return;
            }

            Post post = postService.createPost(SessionManager.getCurrentUserId(), content, mediaUrl);
            if (post != null) {
                StyleUtil.showSuccessAlert("Success", "Post created successfully!");
            } else {
                StyleUtil.showErrorAlert("Error", "Failed to create post");
            }
        } catch (Exception e) {
            StyleUtil.showErrorAlert("Error", "Failed to create post: " + e.getMessage());
        }
    }
}