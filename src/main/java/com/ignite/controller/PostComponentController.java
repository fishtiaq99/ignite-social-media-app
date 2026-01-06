package com.ignite.controller;

import com.ignite.model.Comment;
import com.ignite.model.Post;
import com.ignite.model.User;
import com.ignite.service.CommentService;
import com.ignite.service.LikeService;
import com.ignite.service.PostService;
import com.ignite.service.UserService;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PostComponentController {

    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final UserService userService;

    public PostComponentController() {
        this.postService = new PostService();
        this.likeService = new LikeService();
        this.commentService = new CommentService();
        this.userService = new UserService();
    }

    public VBox createPostBox(Post post, boolean showDelete) {
        VBox postBox = new VBox(10);
        postBox.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 15; -fx-border-color: #0066ff; -fx-border-width: 1; -fx-border-radius: 8;");
        postBox.setMaxWidth(Double.MAX_VALUE);

        // --- Author info ---
        HBox authorBox = new HBox(10);
        authorBox.setAlignment(Pos.CENTER_LEFT);

        Label authorLabel = new Label("Posted by: " + (post.getAuthor() != null ? post.getAuthor().getUsername() : "Unknown"));
        authorLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 12px;");
        authorBox.getChildren().add(authorLabel);

        // --- Content text ---
        Label contentLabel = new Label(post.getContentText());
        contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-wrap-text: true;");
        contentLabel.setMaxWidth(Double.MAX_VALUE);

        // --- Media (image) ---
        if (post.hasMedia()) {
            try {
                ImageView imageView = new ImageView(new Image("file:" + post.getMediaUrl()));
                imageView.setFitWidth(400);
                imageView.setPreserveRatio(true);
                postBox.getChildren().add(imageView);
            } catch (Exception e) {
                Label errorImage = new Label("[Failed to load media: " + post.getMediaUrl() + "]");
                errorImage.setStyle("-fx-text-fill: #ff4444;");
                postBox.getChildren().add(errorImage);
            }
        }

        // --- Like and Comment buttons ---
        HBox interactionBox = createInteractionBox(post);

        // --- metadata ---
        HBox metaBox = new HBox(20);
        metaBox.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label("Posted: " + formatDate(post.getCreationDate()));
        Label likesLabel = new Label("Likes: " + likeService.getLikeCount(post.getPostId()));
        Label commentsLabel = new Label("Comments: " + commentService.getPostComments(post.getPostId()).size());

        String metaStyle = "-fx-text-fill: #888888; -fx-font-size: 12px;";
        dateLabel.setStyle(metaStyle);
        likesLabel.setStyle(metaStyle);
        commentsLabel.setStyle(metaStyle);

        metaBox.getChildren().addAll(dateLabel, likesLabel, commentsLabel);

        // --- actions (delete) ---
        HBox actionsBox = new HBox(10);
        if (showDelete) {
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 12px;");
            deleteButton.setOnAction(e -> handleDeletePost(post.getPostId()));
            actionsBox.getChildren().add(deleteButton);
        }

        postBox.getChildren().addAll(authorBox, contentLabel, interactionBox, metaBox, actionsBox);
        return postBox;
    }

    private HBox createInteractionBox(Post post) {
        HBox interactionBox = new HBox(15);
        interactionBox.setAlignment(Pos.CENTER_LEFT);

        Button likeButton = createLikeButton(post);
        Button commentButton = createCommentButton(post);

        interactionBox.getChildren().addAll(likeButton, commentButton);
        return interactionBox;
    }

    private Button createLikeButton(Post post) {
        Button likeButton = new Button();
        updateLikeButtonAppearance(likeButton, post);

        likeButton.setOnAction(e -> {
            boolean success = likeService.toggleLike(SessionManager.getCurrentUserId(), post.getPostId());
            if (success) {
                updateLikeButtonAppearance(likeButton, post);
                refreshPostDisplay(post);
            } else {
                StyleUtil.showErrorAlert("Error", "Failed to update like");
            }
        });

        return likeButton;
    }

    private void updateLikeButtonAppearance(Button likeButton, Post post) {
        boolean isLiked = likeService.isPostLikedByUser(SessionManager.getCurrentUserId(), post.getPostId());

        if (isLiked) {
            likeButton.setText("❤️ Liked");
            likeButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 12px;");
        } else {
            likeButton.setText("🤍 Like");
            likeButton.setStyle("-fx-background-color: #0066ff; -fx-text-fill: white; -fx-font-size: 12px;");
        }
    }

    private Button createCommentButton(Post post) {
        Button commentButton = new Button("💬 Comment");
        commentButton.setStyle("-fx-background-color: #00ff88; -fx-text-fill: black; -fx-font-size: 12px;");
        commentButton.setOnAction(e -> showCommentsDialog(post));
        return commentButton;
    }

    private void showCommentsDialog(Post post) {
        Dialog<Void> commentsDialog = new Dialog<>();
        commentsDialog.setTitle("Comments - Post #" + post.getPostId());
        commentsDialog.setHeaderText("Comments");

        VBox dialogContent = new VBox(15);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setStyle("-fx-background-color: #0d1b2a;");

        // --- Scrollable comments area ---
        VBox commentsArea = new VBox(10);
        commentsArea.setStyle("-fx-background-color: #1a2b3c; -fx-padding: 10; -fx-border-color: #0066ff; -fx-border-radius: 5;");
        ScrollPane scrollPane = new ScrollPane(commentsArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        refreshCommentsArea(commentsArea, post);

        // --- Add comment input ---
        HBox addCommentBox = new HBox(10);
        addCommentBox.setAlignment(Pos.CENTER_LEFT);

        TextField commentInput = new TextField();
        commentInput.setPromptText("Write a comment...");
        commentInput.setStyle("-fx-background-color: #2a3b4c; -fx-text-fill: white; -fx-pref-width: 300;");

        Button addCommentButton = new Button("Add Comment");
        addCommentButton.setStyle("-fx-background-color: #00ff88; -fx-text-fill: black;");
        addCommentButton.setOnAction(e -> {
            String text = commentInput.getText().trim();
            if (!text.isEmpty()) {
                Comment comment = commentService.addComment(SessionManager.getCurrentUserId(), post.getPostId(), text);
                if (comment != null) {
                    commentInput.clear();
                    refreshCommentsArea(commentsArea, post);
                    refreshPostDisplay(post);
                    StyleUtil.showSuccessAlert("Success", "Comment added!");
                }
            }
        });

        addCommentBox.getChildren().addAll(commentInput, addCommentButton);

        dialogContent.getChildren().addAll(scrollPane, addCommentBox);
        commentsDialog.getDialogPane().setContent(dialogContent);
        commentsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        commentsDialog.getDialogPane().setStyle("-fx-background-color: #0d1b2a;");
        commentsDialog.showAndWait();
    }

    private void refreshCommentsArea(VBox commentsArea, Post post) {
        commentsArea.getChildren().clear();

        var comments = commentService.getPostComments(post.getPostId());
        if (comments.isEmpty()) {
            Label noComments = new Label("No comments yet. Be the first to comment!");
            noComments.setStyle("-fx-text-fill: #888888; -fx-font-style: italic;");
            commentsArea.getChildren().add(noComments);
        } else {
            for (Comment comment : comments) {
                commentsArea.getChildren().add(createCommentBox(comment));
            }
        }
    }

    private VBox createCommentBox(Comment comment) {
        VBox commentBox = new VBox(5);
        commentBox.setStyle("-fx-background-color: #2a3b4c; -fx-padding: 10; -fx-border-radius: 5;");

        User author = userService.getUserById(comment.getUserId());
        String authorName = author != null ? author.getUsername() : "Unknown User";

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label authorLabel = new Label(authorName);
        authorLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label commentIdLabel = new Label("Comment #" + comment.getCommentId());
        commentIdLabel.setStyle("-fx-text-fill: #0066ff; -fx-font-size: 10px;");

        Label dateLabel = new Label(formatDate(comment.getCreationDate()));
        dateLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");

        headerBox.getChildren().addAll(authorLabel, commentIdLabel, dateLabel);

        Label contentLabel = new Label(comment.getContentText());
        contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-wrap-text: true;");
        contentLabel.setMaxWidth(Double.MAX_VALUE);

        HBox actionsBox = new HBox();
        if (SessionManager.getCurrentUserId() == comment.getUserId()) {
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 10px;");
            deleteButton.setOnAction(e -> {
                if (commentService.deleteComment(comment.getCommentId(), SessionManager.getCurrentUserId())) {
                    ((VBox) commentBox.getParent()).getChildren().remove(commentBox);
                    StyleUtil.showSuccessAlert("Success", "Comment deleted!");
                } else {
                    StyleUtil.showErrorAlert("Error", "Failed to delete comment");
                }
            });
            actionsBox.getChildren().add(deleteButton);
        }

        commentBox.getChildren().addAll(headerBox, contentLabel, actionsBox);
        return commentBox;
    }

    private void refreshPostDisplay(Post post) {
        // Optional: implement if parent feed needs dynamic refresh
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "Unknown";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm");
        return sdf.format(date);
    }

    private void handleDeletePost(int postId) {
        if (postService.deletePost(postId, SessionManager.getCurrentUserId())) {
            StyleUtil.showSuccessAlert("Success", "Post deleted successfully!");
        } else {
            StyleUtil.showErrorAlert("Error", "Failed to delete post.");
        }
    }
}
