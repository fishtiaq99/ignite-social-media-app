package com.ignite.controller;

import com.ignite.model.Post;
import com.ignite.service.PostService;
import com.ignite.util.SessionManager;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

public class FeedTabController {

    private PostService postService;

    public FeedTabController() {
        this.postService = new PostService();
    }

    public Tab createTab() {
        Tab feedTab = new Tab("Feed");
        feedTab.setClosable(false);

        VBox feedContent = new VBox(10);
        feedContent.setPadding(new Insets(15));
        feedContent.setStyle("-fx-background-color: transparent;");

        // Title
        Label titleLabel = new Label("Your Feed - Posts from People You Follow");
        titleLabel.setFont(Font.font(20));
        titleLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        // Posts container
        VBox postsContainer = new VBox(15);
        postsContainer.setPadding(new Insets(10));

        // Load posts from followed users
        loadFeedPosts(postsContainer);

        ScrollPane scrollPane = new ScrollPane(postsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        feedContent.getChildren().addAll(titleLabel, scrollPane);
        feedTab.setContent(feedContent);

        return feedTab;
    }

    private void loadFeedPosts(VBox postsContainer) {
        List<Post> feedPosts = postService.getPostsFromFollowedUsers(SessionManager.getCurrentUserId());
        PostComponentController component = new PostComponentController();

        if (feedPosts.isEmpty()) {
            Label placeholder = new Label("No posts to show.");
            placeholder.setStyle("-fx-text-fill: #ccc;");
            postsContainer.getChildren().add(placeholder);
        } else {
            for (Post post : feedPosts) {
                VBox postBox = component.createPostBox(post,true); // <-- FIX: remove boolean
                postsContainer.getChildren().add(postBox);
            }
        }
    }


    private VBox createPostCard(Post post) {
        VBox postCard = new VBox(10);
        postCard.setPadding(new Insets(15));
        postCard.setStyle(
                "-fx-background-color: #1a1a2e;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        Label authorLabel = new Label("Posted by: " + (post.getAuthor() != null ? post.getAuthor().getUsername() : "Unknown"));
        authorLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");

        Label contentLabel = new Label(post.getContentText());
        contentLabel.setStyle("-fx-text-fill: white;");
        contentLabel.setWrapText(true);

        if (post.hasMedia()) {
            Label mediaLabel = new Label("[Image: " + post.getMediaUrl() + "]");
            mediaLabel.setStyle("-fx-text-fill: #cccccc;");
            postCard.getChildren().add(mediaLabel);
        }

        Label dateLabel = new Label("Posted on: " + post.getCreationDate());
        dateLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        postCard.getChildren().addAll(authorLabel, contentLabel, dateLabel);
        return postCard;
    }
}