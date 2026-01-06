package com.ignite.controller;

import com.ignite.model.User;
import com.ignite.service.UserService;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

public class FollowManagementController {

    private UserService userService;
    private TabPane mainTabPane;

    public FollowManagementController() {
        this.userService = new UserService();
    }

    public Tab createTab() {
        Tab followTab = new Tab("Followers/Following");
        followTab.setClosable(false);

        // Main container with tabs for Followers and Following
        mainTabPane = new TabPane();
        mainTabPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-tab-min-height: 32;" +
                        "-fx-tab-max-height: 32;" +
                        "-fx-tab-min-width: 120;" +
                        "-fx-tab-max-width: 120;" +
                        "-fx-padding: 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-tab-header-area-background: transparent;"
        );

        Tab followersTab = createFollowersTab();
        Tab followingTab = createFollowingTab();

        mainTabPane.getTabs().addAll(followersTab, followingTab);

        // Apply styling to tabs
        for (Tab tab : mainTabPane.getTabs()) {
            styleFollowTab(tab, tab.isSelected());
            tab.setOnSelectionChanged(e -> {
                for (Tab t : mainTabPane.getTabs()) {
                    styleFollowTab(t, t.isSelected());
                }
            });
        }

        followTab.setContent(mainTabPane);
        return followTab;
    }

    private void styleFollowTab(Tab tab, boolean selected) {
        if (selected) {
            tab.setStyle(
                    "-fx-background-color: linear-gradient(to right, #003300, #0066ff, #00ff88);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14;" +
                            "-fx-padding: 6 12;" +
                            "-fx-background-radius: 12;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.3), 6,0,0,2);"
            );
        } else {
            tab.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #00ff88;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14;" +
                            "-fx-padding: 6 12;"
            );
        }
    }

    private Tab createFollowersTab() {
        Tab tab = new Tab("Followers");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: transparent;");

        // Get current user's followers
        List<User> followers = userService.getFollowers(SessionManager.getCurrentUserId());
        int followerCount = userService.getFollowerCount(SessionManager.getCurrentUserId());

        Label title = new Label("Your Followers (" + followerCount + ")");
        title.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 18px;");

        VBox followersList = new VBox(10);

        if (followers.isEmpty()) {
            Label emptyLabel = new Label("You don't have any followers yet.");
            emptyLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px; -fx-padding: 20px;");
            followersList.getChildren().add(emptyLabel);
        } else {
            for (User follower : followers) {
                HBox followerItem = createFollowerItem(follower);
                followersList.getChildren().add(followerItem);
            }
        }

        ScrollPane scrollPane = new ScrollPane(followersList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        content.getChildren().addAll(title, scrollPane);
        tab.setContent(content);

        return tab;
    }

    private Tab createFollowingTab() {
        Tab tab = new Tab("Following");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: transparent;");

        // Get users that current user is following
        List<User> following = userService.getFollowing(SessionManager.getCurrentUserId());
        int followingCount = userService.getFollowingCount(SessionManager.getCurrentUserId());

        Label title = new Label("People You Follow (" + followingCount + ")");
        title.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 18px;");

        VBox followingList = new VBox(10);

        if (following.isEmpty()) {
            Label emptyLabel = new Label("You're not following anyone yet.");
            emptyLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px; -fx-padding: 20px;");
            followingList.getChildren().add(emptyLabel);
        } else {
            for (User user : following) {
                HBox followingItem = createFollowingItem(user);
                followingList.getChildren().add(followingItem);
            }
        }

        ScrollPane scrollPane = new ScrollPane(followingList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        content.getChildren().addAll(title, scrollPane);
        tab.setContent(content);

        return tab;
    }

    private HBox createFollowerItem(User follower) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(12));
        item.setStyle(
                "-fx-background-color: #1a1a2e;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );
        item.setAlignment(Pos.CENTER_LEFT);

        // User info
        VBox userInfo = new VBox(5);
        userInfo.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(follower.getUsername());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label emailLabel = new Label(follower.getEmail());
        emailLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        userInfo.getChildren().addAll(nameLabel, emailLabel);

        // Remove button
        Button removeButton = StyleUtil.createSecondaryButton("Remove");
        removeButton.setText("Remove");
        removeButton.setStyle(
                "-fx-background-color: #ff4444;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 16;" +
                        "-fx-background-radius: 8;"
        );
        removeButton.setOnAction(e -> removeFollower(follower.getUserId()));

        // Layout
        HBox.setHgrow(userInfo, javafx.scene.layout.Priority.ALWAYS);
        item.getChildren().addAll(userInfo, removeButton);

        return item;
    }

    private HBox createFollowingItem(User user) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(12));
        item.setStyle(
                "-fx-background-color: #1a1a2e;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );
        item.setAlignment(Pos.CENTER_LEFT);

        // User info
        VBox userInfo = new VBox(5);
        userInfo.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(user.getUsername());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label emailLabel = new Label(user.getEmail());
        emailLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        userInfo.getChildren().addAll(nameLabel, emailLabel);

        // Unfollow button
        Button unfollowButton = StyleUtil.createSecondaryButton("Unfollow");
        unfollowButton.setText("Unfollow");
        unfollowButton.setStyle(
                "-fx-background-color: #ffaa00;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 16;" +
                        "-fx-background-radius: 8;"
        );
        unfollowButton.setOnAction(e -> unfollowUser(user.getUserId()));

        // Layout
        HBox.setHgrow(userInfo, javafx.scene.layout.Priority.ALWAYS);
        item.getChildren().addAll(userInfo, unfollowButton);

        return item;
    }

    private void removeFollower(int followerId) {
        try {
            int currentUserId = SessionManager.getCurrentUserId();
            boolean success = userService.removeFollower(currentUserId, followerId);

            if (success) {
                StyleUtil.showSuccessAlert("Success", "Follower removed successfully!");
                refreshTabs();
            } else {
                StyleUtil.showErrorAlert("Error", "Failed to remove follower.");
            }
        } catch (Exception e) {
            StyleUtil.showErrorAlert("Error", "Failed to remove follower: " + e.getMessage());
        }
    }

    private void unfollowUser(int userId) {
        try {
            int currentUserId = SessionManager.getCurrentUserId();
            boolean success = userService.unfollowUser(currentUserId, userId);

            if (success) {
                StyleUtil.showSuccessAlert("Success", "Unfollowed user successfully!");
                refreshTabs();
            } else {
                StyleUtil.showErrorAlert("Error", "Failed to unfollow user.");
            }
        } catch (Exception e) {
            StyleUtil.showErrorAlert("Error", "Failed to unfollow user: " + e.getMessage());
        }
    }

    private void refreshTabs() {
        // Refresh both tabs to show updated lists
        Tab currentTab = mainTabPane.getSelectionModel().getSelectedItem();

        Tab followersTab = createFollowersTab();
        Tab followingTab = createFollowingTab();

        mainTabPane.getTabs().clear();
        mainTabPane.getTabs().addAll(followersTab, followingTab);

        // Restore selection
        if (currentTab != null && currentTab.getText().equals("Followers")) {
            mainTabPane.getSelectionModel().select(followersTab);
        } else {
            mainTabPane.getSelectionModel().select(followingTab);
        }

        // Reapply styling
        for (Tab tab : mainTabPane.getTabs()) {
            styleFollowTab(tab, tab.isSelected());
            tab.setOnSelectionChanged(e -> {
                for (Tab t : mainTabPane.getTabs()) {
                    styleFollowTab(t, t.isSelected());
                }
            });
        }
    }
}