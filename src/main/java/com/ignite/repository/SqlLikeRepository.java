package com.ignite.repository;

import com.ignite.model.Like;
import com.ignite.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlLikeRepository implements LikeRepository {

    @Override
    public Like save(Like like) {
        String sql = "INSERT INTO Likes (userID, postID, likedFlag) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, like.getUserId());
            stmt.setInt(2, like.getPostId());
            stmt.setBoolean(3, like.isLikedFlag());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        like.setLikeId(rs.getInt(1)); // Fix: setLikeId instead of setId
                        return like;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving like: " + e.getMessage(), e);
        }
        return null;
    }

    // Fix all the mapToLike calls to mapResultSetToLike
    @Override
    public Optional<Like> findById(int likeId) {
        String sql = "SELECT * FROM Likes WHERE likeID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, likeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLike(rs)); // Fix: mapResultSetToLike
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding like by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Like> findByPostId(int postId) {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM Likes WHERE postID = ? AND likedFlag = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    likes.add(mapResultSetToLike(rs)); // Fix: mapResultSetToLike
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding likes by post: " + e.getMessage(), e);
        }
        return likes;
    }

    // Fix the mapping method name
    private Like mapResultSetToLike(ResultSet rs) throws SQLException {
        Like like = new Like();
        like.setLikeId(rs.getInt("likeID")); // Fix: setLikeId
        like.setUserId(rs.getInt("userID"));
        like.setPostId(rs.getInt("postID"));
        like.setLikedFlag(rs.getBoolean("likedFlag"));
        like.setLikeDate(rs.getTimestamp("likeDate")); // Fix: setLikeDate instead of setCreationDate
        return like;
    }

    // Implement other methods...
    @Override
    public Optional<Like> findByUserAndPost(int userId, int postId) {
        String sql = "SELECT * FROM Likes WHERE userID = ? AND postID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLike(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding like by user and post: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Like> findByUserId(int userId) {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM Likes WHERE userID = ? AND likedFlag = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    likes.add(mapResultSetToLike(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding likes by user: " + e.getMessage(), e);
        }
        return likes;
    }

    @Override
    public boolean delete(int likeId) {
        String sql = "DELETE FROM Likes WHERE likeID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, likeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting like: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByUserAndPost(int userId, int postId) {
        String sql = "SELECT 1 FROM Likes WHERE userID = ? AND postID = ? AND likedFlag = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking like existence: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isPostLikedByUser(int userId, int postId) {
        return existsByUserAndPost(userId, postId);
    }

    @Override
    public int getLikeCountByPost(int postId) {
        String sql = "SELECT COUNT(*) FROM Likes WHERE postID = ? AND likedFlag = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting like count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int getLikeCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM Likes WHERE userID = ? AND likedFlag = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user like count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean deleteByUserAndPost(int userId, int postId) {
        String sql = "DELETE FROM Likes WHERE userID = ? AND postID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting like by user and post: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Integer> findLikedPostIdsByUser(int userId) {
        List<Integer> postIds = new ArrayList<>();
        String sql = "SELECT postID FROM Likes WHERE userID = ? AND likedFlag = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    postIds.add(rs.getInt("postID"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding liked post IDs: " + e.getMessage(), e);
        }
        return postIds;
    }


    public List<Like> findAll() {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM Likes WHERE likedFlag = 1";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                likes.add(mapResultSetToLike(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all likes: " + e.getMessage(), e);
        }
        return likes;
    }
}