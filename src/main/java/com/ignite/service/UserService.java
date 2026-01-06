package com.ignite.service;

import com.ignite.model.User;
import com.ignite.repository.SqlUserRepository;
import com.ignite.repository.UserRepository;
import com.ignite.util.PasswordUtil;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new SqlUserRepository();
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOpt.get();
        if (!PasswordUtil.verify(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is deactivated");
        }

        return user;
    }

    public User registerUser(String username, String email, String password, String bio) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // Check if email or username already exists
        if (!userRepository.isEmailAvailable(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (!userRepository.isUsernameAvailable(username)) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Hash password and create user
        String passwordHash = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordHash);
        user.setBio(bio);
        user.setActive(true);      // default active
        user.setApproved(false);   // pending admin approval

        return userRepository.save(user);
    }


    public boolean updateProfile(int userId, String username, String bio) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        user.setUsername(username);
        user.setBio(bio);

        return userRepository.update(user);
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.searchByUsername(query.trim());
    }
    // Add these methods to your UserService class

    public List<User> getFollowers(int userId) {
        return userRepository.findFollowers(userId);
    }

    public List<User> getFollowing(int userId) {
        return userRepository.findFollowing(userId);
    }

    public int getFollowerCount(int userId) {
        return userRepository.getFollowerCount(userId);
    }

    public int getFollowingCount(int userId) {
        return userRepository.getFollowingCount(userId);
    }

    public boolean followUser(int followerId, int followeeId) {
        if (followerId == followeeId) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }
        return userRepository.createFollow(followerId, followeeId);
    }

    public boolean unfollowUser(int followerId, int followeeId) {
        return userRepository.deleteFollow(followerId, followeeId);
    }

    public boolean removeFollower(int userId, int followerId) {
        // Removing a follower means deleting the follow relationship where the other person is following you
        return userRepository.deleteFollow(followerId, userId);
    }

    public boolean isFollowing(int followerId, int followeeId) {
        return userRepository.isFollowing(followerId, followeeId);
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public boolean isEmailAvailable(String email) {
        return userRepository.isEmailAvailable(email);
    }

    public boolean isUsernameAvailable(String username) {
        return userRepository.isUsernameAvailable(username);
    }

    public boolean updateUsername(int userId, String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (newUsername.length() > 50) {
            throw new IllegalArgumentException("Username cannot exceed 50 characters");
        }

        if (!userRepository.isUsernameAvailable(newUsername)) {
            throw new IllegalArgumentException("Username already taken");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        user.setUsername(newUsername.trim());
        return userRepository.update(user);
    }

    public boolean updateEmail(int userId, String newEmail) {
        if (newEmail == null || !isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (newEmail.length() > 100) {
            throw new IllegalArgumentException("Email cannot exceed 100 characters");
        }

        if (!userRepository.isEmailAvailable(newEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        user.setEmail(newEmail.trim());
        return userRepository.update(user);
    }

    public boolean updatePassword(int userId, String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();

        // Verify current password
        if (!PasswordUtil.verify(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Hash new password
        String newPasswordHash = PasswordUtil.hashPassword(newPassword);
        return userRepository.updatePassword(userId, newPasswordHash);
    }

    public boolean updateBio(int userId, String newBio) {
        if (newBio != null && newBio.length() > 500) {
            throw new IllegalArgumentException("Bio cannot exceed 500 characters");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        user.setBio(newBio != null ? newBio.trim() : null);
        return userRepository.update(user);
    }

    public boolean updateProfile(int userId, String username, String email, String bio) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("Username cannot exceed 50 characters");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("Email cannot exceed 100 characters");
        }
        if (bio != null && bio.length() > 500) {
            throw new IllegalArgumentException("Bio cannot exceed 500 characters");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User currentUser = userOpt.get();

        // Check if username is available (if changed)
        if (!currentUser.getUsername().equals(username.trim()) &&
                !userRepository.isUsernameAvailable(username.trim())) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Check if email is available (if changed)
        if (!currentUser.getEmail().equals(email.trim()) &&
                !userRepository.isEmailAvailable(email.trim())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Update user
        currentUser.setUsername(username.trim());
        currentUser.setEmail(email.trim());
        currentUser.setBio(bio != null ? bio.trim() : null);

        return userRepository.update(currentUser);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

}
