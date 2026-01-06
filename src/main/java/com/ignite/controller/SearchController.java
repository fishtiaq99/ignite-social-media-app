package com.ignite.controller;

import com.ignite.service.SearchService;
import com.ignite.service.search.SearchStrategy;
import com.ignite.model.User;
import com.ignite.model.Post;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.text.SimpleDateFormat;
import javafx.stage.Stage;
import java.util.List;
import com.ignite.service.PostService;


public class SearchController {

    private Stage primaryStage;
    private SearchService searchService;
    private VBox mainContainer;
    private TextField searchField;
    private VBox resultsContainer;

    public SearchController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.searchService = new SearchService();
    }

    public VBox createSearchScreen() {
        mainContainer = new VBox();
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(20));

        // Title
        Label titleLabel = StyleUtil.createTitleLabel("Search");

        // Search box
        searchField = StyleUtil.createStyledTextField("Search users (@username) or hashtags (#trending)...");
        searchField.setMaxWidth(500);

        Button searchButton = StyleUtil.createPrimaryButton("Search");
        searchButton.setOnAction(e -> performSearch());
        searchField.setOnAction(e -> performSearch());

        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER);
        searchContainer.getChildren().addAll(searchField, searchButton);

        // Results inside a scrollable pane
        resultsContainer = new VBox(15);
        resultsContainer.setAlignment(Pos.TOP_CENTER);
        resultsContainer.setMaxWidth(700);

        ScrollPane scrollPane = new ScrollPane(resultsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefHeight(600);

        mainContainer.getChildren().addAll(
                titleLabel,
                searchContainer,
                scrollPane
        );

        return mainContainer;
    }

    private void performSearch() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            StyleUtil.showErrorAlert("Search Error", "Please enter a search term");
            return;
        }

        // Clear previous results
        resultsContainer.getChildren().clear();

        // Show loading indicator
        Label loadingLabel = new Label("Searching...");
        loadingLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 14px;");
        resultsContainer.getChildren().add(loadingLabel);

        // Perform search in background
        new Thread(() -> {
            try {
                Thread.sleep(100); // Small delay for better UX

                int currentUserId = SessionManager.getCurrentUserId();
                SearchStrategy.SearchResult results = searchService.searchAll(query, currentUserId);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    resultsContainer.getChildren().clear();
                    displaySearchResults(results, query);
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displaySearchResults(SearchStrategy.SearchResult results, String query) {
        if (results.isEmpty()) {
            Label noResultsLabel = new Label("No results found for \"" + query + "\"");
            noResultsLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 16px; -fx-padding: 40;");
            resultsContainer.getChildren().add(noResultsLabel);
            return;
        }

        // Display users section
        if (results.hasUsers()) {
            Label usersSectionLabel = new Label("Users (" + results.getUsers().size() + ")");
            usersSectionLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
            resultsContainer.getChildren().add(usersSectionLabel);

            for (User user : results.getUsers()) {
                resultsContainer.getChildren().add(createUserCard(user));
            }
        }

        // Display posts section (for hashtag searches)
        if (results.hasPosts()) {
            Label postsSectionLabel = new Label("Posts with " + query + " (" + results.getPosts().size() + ")");
            postsSectionLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 20 0 5 0;");
            resultsContainer.getChildren().add(postsSectionLabel);

            for (Post post : results.getPosts()) {
                resultsContainer.getChildren().add(createPostCard(post));
            }
        }
    }

    private VBox createUserCard(User user) {
        VBox userCard = new VBox(8);
        userCard.setStyle("-fx-background-color: #1a2d3f; -fx-padding: 15; -fx-background-radius: 10;");
        userCard.setMaxWidth(600);

        // Username and follow button row
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setSpacing(10);

        Label usernameLabel = new Label("@" + user.getUsername());
        usernameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Follow button
        Button followButton = createFollowButton(user);

        HBox.setHgrow(usernameLabel, Priority.ALWAYS);
        topRow.getChildren().addAll(usernameLabel, followButton);

        // Bio
        if (user.getBio() != null && !user.getBio().isEmpty()) {
            Label bioLabel = new Label(user.getBio());
            bioLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");
            bioLabel.setWrapText(true);
            userCard.getChildren().addAll(topRow, bioLabel);
        } else {
            userCard.getChildren().add(topRow);
        }

        return userCard;
    }

    private Button createFollowButton(User user) {
        int currentUserId = SessionManager.getCurrentUserId();
        boolean isFollowing = searchService.isFollowing(currentUserId, user.getUserId());

        Button followButton = new Button(isFollowing ? "Following" : "Follow");
        followButton.setStyle(isFollowing ?
                "-fx-background-color: #00ff88; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 5 15;" :
                "-fx-background-color: #0066ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15;"
        );

        followButton.setOnAction(e -> {
            boolean success = searchService.toggleFollow(currentUserId, user.getUserId());
            if (success) {
                // Update button text and style
                boolean nowFollowing = searchService.isFollowing(currentUserId, user.getUserId());
                followButton.setText(nowFollowing ? "Following" : "Follow");
                followButton.setStyle(nowFollowing ?
                        "-fx-background-color: #00ff88; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 5 15;" :
                        "-fx-background-color: #0066ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15;"
                );
            } else {
                StyleUtil.showErrorAlert("Error", "Failed to update follow status");
            }
        });

        return followButton;
    }

    private VBox createPostCard(Post post) {
        VBox postCard = new VBox(10);
        postCard.setStyle("-fx-background-color: #1a2d3f; -fx-padding: 15; -fx-background-radius: 10;");
        postCard.setMaxWidth(600);

        // Author info and follow button
        HBox authorRow = new HBox();
        authorRow.setAlignment(Pos.CENTER_LEFT);
        authorRow.setSpacing(10);

        Label authorLabel = new Label("Posted by @" + (post.getAuthor() != null ? post.getAuthor().getUsername() : "Unknown"));
        authorLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 14px;");

        // Follow button for post author (if not current user and author exists)
        if (post.getAuthor() != null && post.getAuthor().getUserId() != SessionManager.getCurrentUserId()) {
            Button followAuthorButton = createFollowButton(post.getAuthor());
            authorRow.getChildren().addAll(authorLabel, followAuthorButton);
        } else {
            authorRow.getChildren().add(authorLabel);
        }

        HBox.setHgrow(authorLabel, Priority.ALWAYS);

        // Post content
        Label contentLabel = new Label(post.getContentText());
        contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        contentLabel.setWrapText(true);

        // Post stats (likes, date)
        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        // Like count - get actual like count from database
        int likeCount = searchService.getPostService().getPostRepository().getLikeCount(post.getPostId());
        Label likesLabel = new Label("❤️ " + likeCount + " likes");
        likesLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px;");

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
        Label dateLabel = new Label(dateFormat.format(post.getCreationDate()));
        dateLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        statsRow.getChildren().addAll(likesLabel, dateLabel);

        // Hashtags - extract and display them
        List<String> hashtags = post.extractHashtags();
        if (!hashtags.isEmpty()) {
            Label hashtagsLabel = new Label("Tags: " + String.join(" ", hashtags.stream()
                    .map(hashtag -> "#" + hashtag)
                    .toList()));
            hashtagsLabel.setStyle("-fx-text-fill: #00d4ff; -fx-font-size: 12px;");
            postCard.getChildren().addAll(authorRow, contentLabel, statsRow, hashtagsLabel);
        } else {
            postCard.getChildren().addAll(authorRow, contentLabel, statsRow);
        }

        return postCard;
    }

    // Add this helper method to SearchService or access it directly
    private PostService getPostService() {
        return searchService.getPostService(); // You'll need to add this getter to SearchService
    }
}