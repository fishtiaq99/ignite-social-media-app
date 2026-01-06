package com.ignite.repository;

import com.ignite.model.Follow;
import com.ignite.model.User;
import com.ignite.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlFollowRepository implements FollowRepository {

    @Override
    public Follow save(Follow follow) {
        String sql = "INSERT INTO Follows (followerID, followeeID) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, follow.getFollowerId());
            stmt.setInt(2, follow.getFolloweeId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return follow;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving follow: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Optional<Follow> findById(int followerId, int followeeId) {
        String sql = "SELECT * FROM Follows WHERE followerID = ? AND followeeID = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, followerId);
            stmt.setInt(2, followeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFollow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding follow: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(int followerId, int followeeId) {
        String sql = "DELETE FROM Follows WHERE followerID = ? AND followeeID = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, followerId);
            stmt.setInt(2, followeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting follow: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isFollowing(int followerId, int followeeId) {
        String sql = "SELECT 1 FROM Follows WHERE followerID = ? AND followeeID = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, followerId);
            stmt.setInt(2, followeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking follow status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Integer> findFollowerIds(int userId) {
        List<Integer> followerIds = new ArrayList<>();
        String sql = "SELECT followerID FROM Follows WHERE followeeID = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    followerIds.add(rs.getInt("followerID"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding follower IDs: " + e.getMessage(), e);
        }
        return followerIds;
    }

    @Override
    public List<Integer> findFollowingIds(int userId) {
        List<Integer> followingIds = new ArrayList<>();
        String sql = "SELECT followeeID FROM Follows WHERE followerID = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    followingIds.add(rs.getInt("followeeID"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding following IDs: " + e.getMessage(), e);
        }
        return followingIds;
    }

    @Override
    public int getFollowerCount(int userId) {
        String sql = "SELECT COUNT(*) FROM Follows WHERE followeeID = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting follower count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int getFollowingCount(int userId) {
        String sql = "SELECT COUNT(*) FROM Follows WHERE followerID = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting following count: " + e.getMessage(), e);
        }
        return 0;
    }

    // Helper to map a ResultSet to Follow
    private Follow mapResultSetToFollow(ResultSet rs) throws SQLException {
        Follow follow = new Follow();
        follow.setFollowerId(rs.getInt("followerID"));
        follow.setFolloweeId(rs.getInt("followeeID"));
        follow.setFollowDate(rs.getTimestamp("followDate"));
        follow.setAccepted(true); // Default for now
        return follow;
    }

    // Stub implementations for follow requests
    @Override
    public boolean acceptFollowRequest(int followerId, int followeeId) { return true; }

    @Override
    public boolean rejectFollowRequest(int followerId, int followeeId) { return delete(followerId, followeeId); }

    @Override
    public boolean isFollowRequestPending(int followerId, int followeeId) { return false; }

    @Override
    public List<Integer> findPendingFollowerIds(int userId) { return new ArrayList<>(); }

    @Override
    public int getPendingFollowRequestCount(int userId) { return 0; }

    @Override
    public List<Follow> findAllFollows() { return new ArrayList<>(); }

    @Override
    public List<Follow> findPendingFollowRequests() { return new ArrayList<>(); }

}
