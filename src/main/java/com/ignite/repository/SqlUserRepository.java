package com.ignite.repository;

import com.ignite.model.User;
import com.ignite.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlUserRepository implements UserRepository {

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("userID"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setBio(rs.getString("bio"));
        user.setApproved(rs.getBoolean("isApproved"));
        user.setActive(rs.getBoolean("isActive"));
        return user;
    }

    // -------------------- CRUD --------------------
    @Override
    public User save(User user) {
        String sql = "INSERT INTO Users (username, email, password, bio, joinDate, isApproved, isActive) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getBio());
            stmt.setTimestamp(5, new Timestamp(user.getJoinDate().getTime()));
            stmt.setBoolean(6, user.isApproved());
            stmt.setBoolean(7, user.isActive());

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) user.setUserId(keys.getInt(1));
            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM Users WHERE userID = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? Optional.of(mapUser(rs)) : Optional.empty();
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? Optional.of(mapUser(rs)) : Optional.empty();
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? Optional.of(mapUser(rs)) : Optional.empty();
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM Users";
        List<User> list = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException ignored) {}
        return list;
    }



    @Override
    public boolean update(User user) {
        String sql = "UPDATE Users SET username = ?, email = ?, bio = ? WHERE userID = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getBio());
            stmt.setInt(4, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean delete(int userId) {
        String sql = "DELETE FROM Users WHERE userID = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // -------------------- Activation --------------------
    @Override
    public boolean deactivateUser(int userId) { return toggleActive(userId, false); }
    @Override
    public boolean activateUser(int userId) { return toggleActive(userId, true); }
    private boolean toggleActive(int userId, boolean active) {
        String sql = "UPDATE Users SET isActive = ? WHERE userID = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // -------------------- Password --------------------
    @Override
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE Users SET password = ? WHERE userID = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // -------------------- Follow --------------------
    @Override
    public List<User> findFollowers(int userId) {
        String sql = "SELECT u.* FROM Users u JOIN Follows f ON u.userID = f.followerID WHERE f.followeeID = ?";
        List<User> list = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    @Override
    public List<User> findFollowing(int userId) {
        String sql = "SELECT u.* FROM Users u JOIN Follows f ON u.userID = f.followeeID WHERE f.followerID = ?";
        List<User> list = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    @Override
    public boolean isFollowing(int followerId, int followeeId) {
        String sql = "SELECT 1 FROM Follows WHERE followerID = ? AND followeeID = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followeeId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean createFollow(int followerId, int followeeId) {
        String sql = "INSERT INTO Follows (followerID, followeeID) VALUES (?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteFollow(int followerId, int followeeId) {
        String sql = "DELETE FROM Follows WHERE followerID = ? AND followeeID = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int getFollowerCount(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM Follows WHERE followeeID = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException ignored) {}
        return 0;
    }

    @Override
    public int getFollowingCount(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM Follows WHERE followerID = ?";
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException ignored) {}
        return 0;
    }

    // -------------------- Search --------------------
    @Override
    public List<User> searchByUsername(String query) {
        String sql = "SELECT * FROM Users WHERE username LIKE ? AND isActive = 1";
        List<User> list = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    @Override
    public List<User> searchByEmail(String query) {
        String sql = "SELECT * FROM Users WHERE email LIKE ? AND isActive = 1";
        List<User> list = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException ignored) {}
        return list;
    }

    @Override
    public List<User> findActiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE isActive = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    @Override
    public boolean isEmailAvailable(String email) { return !findByEmail(email).isPresent(); }
    @Override
    public boolean isUsernameAvailable(String username) { return !findByUsername(username).isPresent(); }

    @Override
    public boolean updateProfile(int userId, String username, String bio, String avatarUrl, boolean isPrivate) {
        // Since your database doesn't have avatarUrl and isPrivate, remove them
        String sql = "UPDATE Users SET username = ?, bio = ? WHERE userID = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, bio);
            stmt.setInt(3, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user profile: " + e.getMessage(), e);
        }
    }

    // NEW: Get unapproved users
    @Override
    public List<User> findUnapprovedUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE isApproved = 0 AND isActive = 1 ORDER BY joinDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // NEW: Approve a user
    @Override
    public boolean approveUser(int userId) {
        String sql = "UPDATE Users SET isApproved = 1 WHERE userID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // NEW: Admin delete user (soft delete: mark inactive + optionally unapprove)
    @Override
    public boolean adminDeleteUser(int userId) {
        String sql = "UPDATE Users SET isActive = 0 WHERE userID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // NEW: Count all users
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) AS total FROM Users WHERE isActive = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // NEW: Count unapproved users
    @Override
    public int countUnapproved() {
        String sql = "SELECT COUNT(*) AS total FROM Users WHERE isApproved = 0 AND isActive = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // NEW: Get inactive users
    @Override
    public List<User> findInactiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE isActive = 0 ORDER BY joinDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    // NEW: Count active users
    @Override
    public int countActive() {
        String sql = "SELECT COUNT(*) AS total FROM Users WHERE isActive = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // NEW: Count inactive users
    @Override
    public int countInactive() {
        String sql = "SELECT COUNT(*) AS total FROM Users WHERE isActive = 0";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
