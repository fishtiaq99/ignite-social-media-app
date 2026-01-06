package com.ignite.controller;

import com.ignite.model.User;
import com.ignite.service.UserService;
import com.ignite.util.SessionManager;
import com.ignite.util.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class EditProfileTabController {

    private UserService userService;
    private TextField usernameField;
    private TextField emailField;
    private TextArea bioArea;
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Label charCountLabel;

    public EditProfileTabController() {
        this.userService = new UserService();
    }

    public Tab createTab() {
        Tab editProfileTab = new Tab("Edit Profile");
        editProfileTab.setClosable(false);

        VBox mainContainer = new VBox();
        StyleUtil.applyDarkTheme(mainContainer);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setSpacing(25);
        mainContainer.setPadding(new Insets(20));

        // Title
        Label titleLabel = StyleUtil.createTitleLabel("Edit Your Profile");

        // Create form sections
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.TOP_CENTER);
        formContainer.setMaxWidth(500);

        // Basic Info Section
        VBox basicInfoSection = createBasicInfoSection();

        // Bio Section
        VBox bioSection = createBioSection();

        // Password Section
        VBox passwordSection = createPasswordSection();

        // Action buttons
        HBox buttonContainer = createButtonContainer();

        formContainer.getChildren().addAll(
                basicInfoSection, bioSection, passwordSection, buttonContainer
        );

        mainContainer.getChildren().addAll(titleLabel, formContainer);

        // Load current user data
        loadCurrentUserData();

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        editProfileTab.setContent(scrollPane);

        return editProfileTab;
    }

    private VBox createBasicInfoSection() {
        VBox section = new VBox(12);
        section.setAlignment(Pos.TOP_LEFT);

        Label sectionLabel = new Label("Basic Information");
        sectionLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Username field
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        usernameField = StyleUtil.createStyledTextField("Enter new username");
        usernameField.setMaxWidth(400);

        // Email field
        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        emailField = StyleUtil.createStyledTextField("Enter new email");
        emailField.setMaxWidth(400);

        section.getChildren().addAll(
                sectionLabel, usernameLabel, usernameField, emailLabel, emailField
        );

        return section;
    }

    private VBox createBioSection() {
        VBox section = new VBox(12);
        section.setAlignment(Pos.TOP_LEFT);

        Label sectionLabel = new Label("Bio");
        sectionLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label bioLabel = new Label("Tell others about yourself");
        bioLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        bioArea = new TextArea();
        bioArea.setPromptText("Write something about yourself...");
        bioArea.setWrapText(true);
        bioArea.setStyle(
                "-fx-background-color: #0d1b2a; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 12 15; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #404040; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 10;"
        );
        bioArea.setPrefRowCount(3);
        bioArea.setMaxWidth(400);

        // Character counter
        charCountLabel = new Label("0/500");
        charCountLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        bioArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int length = newValue.length();
            charCountLabel.setText(length + "/500");
            if (length > 500) {
                charCountLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px;");
            } else {
                charCountLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
            }
        });

        section.getChildren().addAll(sectionLabel, bioLabel, bioArea, charCountLabel);
        return section;
    }

    private VBox createPasswordSection() {
        VBox section = new VBox(12);
        section.setAlignment(Pos.TOP_LEFT);

        Label sectionLabel = new Label("Change Password");
        sectionLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label instructionLabel = new Label("Leave blank if you don't want to change your password");
        instructionLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px; -fx-font-style: italic;");

        // Current password
        Label currentPasswordLabel = new Label("Current Password");
        currentPasswordLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Enter current password");
        currentPasswordField.setStyle(StyleUtil.createStyledTextField("").getStyle());
        currentPasswordField.setMaxWidth(400);

        // New password
        Label newPasswordLabel = new Label("New Password");
        newPasswordLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        newPasswordField.setStyle(StyleUtil.createStyledTextField("").getStyle());
        newPasswordField.setMaxWidth(400);

        // Confirm password
        Label confirmPasswordLabel = new Label("Confirm New Password");
        confirmPasswordLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        confirmPasswordField.setStyle(StyleUtil.createStyledTextField("").getStyle());
        confirmPasswordField.setMaxWidth(400);

        section.getChildren().addAll(
                sectionLabel, instructionLabel,
                currentPasswordLabel, currentPasswordField,
                newPasswordLabel, newPasswordField,
                confirmPasswordLabel, confirmPasswordField
        );

        return section;
    }

    private HBox createButtonContainer() {
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);

        Button saveButton = StyleUtil.createPrimaryButton("Save Changes");
        saveButton.setOnAction(e -> saveProfileChanges());

        Button resetButton = StyleUtil.createSecondaryButton("Reset");
        resetButton.setOnAction(e -> loadCurrentUserData());

        buttonContainer.getChildren().addAll(saveButton, resetButton);
        return buttonContainer;
    }

    private void loadCurrentUserData() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            emailField.setText(currentUser.getEmail());
            bioArea.setText(currentUser.getBio() != null ? currentUser.getBio() : "");

            // Clear password fields
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            // Update character count
            charCountLabel.setText((currentUser.getBio() != null ? currentUser.getBio().length() : 0) + "/500");
            charCountLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
        }
    }

    private void saveProfileChanges() {
        try {
            int currentUserId = SessionManager.getCurrentUserId();
            boolean changesMade = false;

            // Validate basic info
            String newUsername = usernameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newBio = bioArea.getText().trim();

            // Check if basic info changed
            User currentUser = SessionManager.getCurrentUser();
            boolean basicInfoChanged = !newUsername.equals(currentUser.getUsername()) ||
                    !newEmail.equals(currentUser.getEmail()) ||
                    !((newBio.isEmpty() && currentUser.getBio() == null) ||
                            (newBio.equals(currentUser.getBio() != null ? currentUser.getBio() : "")));

            // Update basic info if changed
            if (basicInfoChanged) {
                boolean success = userService.updateProfile(currentUserId, newUsername, newEmail, newBio);
                if (success) {
                    changesMade = true;
                    // Update session user
                    currentUser.setUsername(newUsername);
                    currentUser.setEmail(newEmail);
                    currentUser.setBio(newBio.isEmpty() ? null : newBio);
                    SessionManager.setCurrentUser(currentUser);
                }
            }

            // Handle password change
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            boolean passwordFieldsFilled = !currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty();

            if (passwordFieldsFilled) {
                if (currentPassword.isEmpty()) {
                    StyleUtil.showErrorAlert("Error", "Please enter your current password to change it");
                    return;
                }

                if (newPassword.isEmpty()) {
                    StyleUtil.showErrorAlert("Error", "Please enter a new password");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    StyleUtil.showErrorAlert("Error", "New passwords do not match");
                    return;
                }

                boolean passwordSuccess = userService.updatePassword(currentUserId, currentPassword, newPassword);
                if (passwordSuccess) {
                    changesMade = true;
                    // Clear password fields
                    currentPasswordField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                }
            }

            if (changesMade) {
                StyleUtil.showSuccessAlert("Success", "Profile updated successfully!");
            } else {
                StyleUtil.showSuccessAlert("Info", "No changes were made.");
            }

        } catch (Exception e) {
            StyleUtil.showErrorAlert("Update Failed", e.getMessage());
        }
    }
}