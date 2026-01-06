package com.ignite.repository;

import com.ignite.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    // CRUD
    User save(User user);
    Optional<User> findById(int userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    boolean update(User user);
    boolean delete(int userId);

    // Activation
    boolean deactivateUser(int userId);
    boolean activateUser(int userId);

    // Password
    boolean updatePassword(int userId, String newPassword);

    // Follow
    List<User> findFollowers(int userId);
    List<User> findFollowing(int userId);
    boolean isFollowing(int followerId, int followeeId);
    boolean createFollow(int followerId, int followeeId);
    boolean deleteFollow(int followerId, int followeeId);
    int getFollowerCount(int userId);
    int getFollowingCount(int userId);

    // Search
    List<User> searchByUsername(String query);
    List<User> searchByEmail(String query);
    List<User> findActiveUsers();

    // Validation
    boolean isEmailAvailable(String email);
    boolean isUsernameAvailable(String username);

    // Profile
    boolean updateProfile(int userId, String username, String bio, String avatarUrl, boolean isPrivate);

    boolean approveUser(int userId);
    boolean adminDeleteUser(int userId);
    int countAll();
    int countUnapproved();

    List<User> findInactiveUsers();
    List<User> findUnapprovedUsers();

    int countActive();
    int countInactive();

}
