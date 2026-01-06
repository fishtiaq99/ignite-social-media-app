package com.ignite.controller;

import com.ignite.model.Post;
import com.ignite.model.User;
import com.ignite.service.PostService;
import com.ignite.util.SessionManager;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.util.List;

public class HomeTabController {

    private final PostService postService;

    public HomeTabController() {
        this.postService = new PostService();
    }

    public Tab createTab() {
        Tab tab = new Tab("Home");
        tab.setClosable(false);

        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: transparent;"); // make background transparent

        VBox userDetailsBox = createUserDetailsSection();
        container.getChildren().add(userDetailsBox);

        VBox postsSection = createUserPostsSection();
        container.getChildren().add(postsSection);

        tab.setContent(container);
        return tab;
    }

    private VBox createUserDetailsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle(
                "-fx-background-color: rgba(26,26,46,0.8);" + // floating effect
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.2), 8,0,0,2);"
        );

        Label title = new Label("Your Profile");
        title.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 18;");

        User user = SessionManager.getCurrentUser();
        Label idLabel = new Label("User ID: " + user.getUserId());
        Label nameLabel = new Label("Username: " + user.getUsername());
        Label bioLabel = new Label("Bio: " + (user.getBio() != null ? user.getBio() : "No bio yet"));

        String infoStyle = "-fx-text-fill: #cccccc; -fx-font-size: 14;";
        idLabel.setStyle(infoStyle);
        nameLabel.setStyle(infoStyle);
        bioLabel.setStyle(infoStyle);

        section.getChildren().addAll(title, idLabel, nameLabel, bioLabel);
        return section;
    }

    private VBox createUserPostsSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(10));

        Label sectionTitle = new Label("Your Posts");
        sectionTitle.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 18;");

        VBox postsContainer = new VBox(15);

        List<Post> posts = postService.getPostsByUser(SessionManager.getCurrentUserId());

        if (posts.isEmpty()) {
            Label empty = new Label("No posts yet.");
            empty.setStyle("-fx-text-fill: #888888; -fx-font-style: italic;");
            postsContainer.getChildren().add(empty);
        } else {
            PostComponentController postComponent = new PostComponentController();
            for (Post post : posts) {
                VBox postBox = postComponent.createPostBox(post, true);

                postBox.setStyle(
                        "-fx-background-color: #0d1b2a;" +
                                "-fx-padding: 15;" +
                                "-fx-border-radius: 10;" +
                                "-fx-border-color: #00ff88;" +
                                "-fx-border-width: 1;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.2), 10,0,0,2);"
                );
                postsContainer.getChildren().add(postBox);
            }
        }

        ScrollPane scroll = new ScrollPane(postsContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 0;" // remove that ugly scroll rectangle
        );

        section.getChildren().addAll(sectionTitle, scroll);
        return section;
    }
}
