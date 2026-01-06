package com.ignite.repository;
import java.util.Collections;
import com.ignite.model.Post;
import com.ignite.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlPostRepository implements PostRepository {

    @Override
    public Post save(Post post) {
        String sql = "INSERT INTO Posts (userID, contentText, mediaURL) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getContentText());
            stmt.setString(3, post.getMediaUrl());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        post.setPostId(rs.getInt(1));
                        return post;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving post: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Optional<Post> findById(int postId) {
        String sql = "SELECT * FROM Posts WHERE postID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding post by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Post> findByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM Posts WHERE userID = ? ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding posts by user: " + e.getMessage(), e);
        }
        return posts;
    }

    @Override
    public List<Post> findByUserIds(List<Integer> userIds) {
        List<Post> posts = new ArrayList<>();
        if (userIds.isEmpty()) return posts;

        String placeholders = String.join(",", Collections.nCopies(userIds.size(), "?"));
        String sql = "SELECT * FROM Posts WHERE userID IN (" + placeholders + ") ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < userIds.size(); i++) {
                stmt.setInt(i + 1, userIds.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding posts by user IDs: " + e.getMessage(), e);
        }
        return posts;
    }

    @Override
    public List<Post> findTrendingPosts(int hours, int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = """
            SELECT TOP (?) p.*, COUNT(l.likeID) as likeCount 
            FROM Posts p 
            LEFT JOIN Likes l ON p.postID = l.postID 
            WHERE p.creationDate >= DATEADD(HOUR, -?, GETDATE())
            GROUP BY p.postID, p.userID, p.contentText, p.mediaURL, p.creationDate, p.lastEditedDate
            HAVING COUNT(l.likeID) >= 3
            ORDER BY likeCount DESC
            """;

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, hours);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding trending posts: " + e.getMessage(), e);
        }
        return posts;
    }

    @Override
    public List<Post> searchByContent(String query) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM Posts WHERE contentText LIKE ? ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching posts: " + e.getMessage(), e);
        }
        return posts;
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM Posts ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all posts: " + e.getMessage(), e);
        }
        return posts;
    }

    @Override
    public boolean update(Post post) {
        String sql = "UPDATE Posts SET contentText = ?, mediaURL = ?, lastEditedDate = GETDATE() WHERE postID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, post.getContentText());
            stmt.setString(2, post.getMediaUrl());
            stmt.setInt(3, post.getPostId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating post: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int postId) {
        String sql = "DELETE FROM Posts WHERE postID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting post: " + e.getMessage(), e);
        }
    }

    @Override
    public int getPostCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM Posts WHERE userID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting post count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<Post> findHomeFeedPosts(List<Integer> followedUserIds, int limit, int offset) {
        // Simplified implementation - just get posts from followed users
        return findByUserIds(followedUserIds);
    }

    @Override
    public List<Post> findRecentPosts(int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM Posts ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding recent posts: " + e.getMessage(), e);
        }
        return posts;
    }


    @Override
    public List<Post> findByHashtag(String hashtag) {
        List<Post> posts = new ArrayList<>();

        // More precise query to find posts with the exact hashtag
        String sql = """
        SELECT p.* 
        FROM Posts p
        WHERE p.contentText LIKE ? OR p.contentText LIKE ? OR p.contentText LIKE ?
        ORDER BY p.creationDate DESC
        """;

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Search for different variations of the hashtag
            stmt.setString(1, "%#" + hashtag + " %");     // Hashtag with space after
            stmt.setString(2, "%#" + hashtag + "%");      // Hashtag anywhere
            stmt.setString(3, "%#" + hashtag);            // Hashtag at end

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding posts by hashtag: " + e.getMessage(), e);
        }
        return posts;
    }


    @Override
    public List<Post> findWithMedia() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM Posts WHERE mediaURL IS NOT NULL ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding posts with media: " + e.getMessage(), e);
        }
        return posts;
    }

    @Override
    public boolean incrementLikeCount(int postId) {
        // This would be handled by the LikeRepository
        return true;
    }

    @Override
    public boolean decrementLikeCount(int postId) {
        // This would be handled by the LikeRepository
        return true;
    }

    @Override
    public boolean incrementCommentCount(int postId) {
        // This would be handled by the CommentRepository
        return true;
    }

    @Override
    public boolean decrementCommentCount(int postId) {
        // This would be handled by the CommentRepository
        return true;
    }

    @Override
    public int getLikeCount(int postId) {
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
    public int getCommentCount(int postId) {
        String sql = "SELECT COUNT(*) FROM Comments WHERE postID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting comment count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean addHashtagToPost(int postId, int hashtagId) {
        String sql = "INSERT INTO PostHashtags (postID, hashtagID) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, hashtagId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding hashtag to post: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeHashtagFromPost(int postId, int hashtagId) {
        String sql = "DELETE FROM PostHashtags WHERE postID = ? AND hashtagID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, hashtagId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing hashtag from post: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> findPostHashtags(int postId) {
        List<String> hashtags = new ArrayList<>();
        String sql = """
            SELECT h.phrase FROM Hashtags h
            JOIN PostHashtags ph ON h.hashtagID = ph.hashtagID
            WHERE ph.postID = ?
            """;

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    hashtags.add(rs.getString("phrase"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding post hashtags: " + e.getMessage(), e);
        }
        return hashtags;
    }

    // Helper method to map ResultSet to Post object
    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setPostId(rs.getInt("postID"));
        post.setUserId(rs.getInt("userID"));
        post.setContentText(rs.getString("contentText"));
        post.setMediaUrl(rs.getString("mediaURL"));
        post.setCreationDate(rs.getTimestamp("creationDate"));
        post.setLastEditedDate(rs.getTimestamp("lastEditedDate"));
        return post;
    }

    // NEW: Get reported posts
    @Override
    public List<Post> findReportedPosts() {
        List<Post> posts = new ArrayList<>();
        String sql = """
    SELECT p.* FROM Posts p 
    INNER JOIN Reports r ON p.postID = r.postID 
    WHERE r.statusFlag = 'PENDING'
    GROUP BY p.postID, p.userID, p.contentText, p.mediaURL, p.creationDate, p.lastEditedDate
    ORDER BY COUNT(r.reportID) DESC
    """;


        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // NEW: Count all posts
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) AS total FROM Posts";

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

    // NEW: Admin delete post
    public boolean adminDeletePost(int postId) {
        // Count reports directly on the post
        String countPostReportsSql = "SELECT COUNT(*) FROM Reports WHERE postID = ?";

        // Count reports on comments under this post
        String countCommentReportsSql = """
        SELECT COUNT(*)
        FROM Reports r
        INNER JOIN Comments c ON r.commentID = c.commentID
        WHERE c.postID = ?
        """;

        // Archive post reports into ResolvedReports
        String insertPostReportsSql = """
        INSERT INTO ResolvedReports (
            reporterUserID,
            contentType,
            contentID,
            reportDate,
            resolvedDate,
            reason,
            reportID
        )
        SELECT
            reporterUserID,
            'POST'    AS contentType,
            postID    AS contentID,
            reportDate,
            GETDATE() AS resolvedDate,
            reason,
            reportID
        FROM Reports
        WHERE postID = ?
        """;

        // Archive comment reports under this post into ResolvedReports
        String insertCommentReportsSql = """
        INSERT INTO ResolvedReports (
            reporterUserID,
            contentType,
            contentID,
            reportDate,
            resolvedDate,
            reason,
            reportID
        )
        SELECT
            r.reporterUserID,
            'COMMENT'       AS contentType,
            r.commentID     AS contentID,
            r.reportDate,
            GETDATE()       AS resolvedDate,
            r.reason,
            r.reportID
        FROM Reports r
        INNER JOIN Comments c ON r.commentID = c.commentID
        WHERE c.postID = ?
        """;

        // Delete reports on this post
        String deletePostReportsSql = "DELETE FROM Reports WHERE postID = ?";

        // Delete reports on comments under this post
        String deleteCommentReportsSql = """
        DELETE FROM Reports
        WHERE commentID IN (SELECT commentID FROM Comments WHERE postID = ?)
        """;

        // Finally delete the post (Comments likely removed via ON DELETE CASCADE)
        String deletePostSql = "DELETE FROM Posts WHERE postID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try {
                int postReportCount = 0;
                int commentReportCount = 0;

                // 1) Count post reports
                try (PreparedStatement psCountPost = conn.prepareStatement(countPostReportsSql)) {
                    psCountPost.setInt(1, postId);
                    try (ResultSet rs = psCountPost.executeQuery()) {
                        if (rs.next()) {
                            postReportCount = rs.getInt(1);
                        }
                    }
                }

                // 2) Count reports on comments under this post
                try (PreparedStatement psCountComment = conn.prepareStatement(countCommentReportsSql)) {
                    psCountComment.setInt(1, postId);
                    try (ResultSet rs = psCountComment.executeQuery()) {
                        if (rs.next()) {
                            commentReportCount = rs.getInt(1);
                        }
                    }
                }

                int totalReports = postReportCount + commentReportCount;

                // 3) Only if there ARE reports, archive and delete them
                if (totalReports > 0) {
                    // 3a) archive post reports
                    try (PreparedStatement psInsertPostReports = conn.prepareStatement(insertPostReportsSql)) {
                        psInsertPostReports.setInt(1, postId);
                        psInsertPostReports.executeUpdate();
                    }

                    // 3b) archive comment reports
                    try (PreparedStatement psInsertCommentReports = conn.prepareStatement(insertCommentReportsSql)) {
                        psInsertCommentReports.setInt(1, postId);
                        psInsertCommentReports.executeUpdate();
                    }

                    // 3c) delete post reports
                    try (PreparedStatement psDeletePostReports = conn.prepareStatement(deletePostReportsSql)) {
                        psDeletePostReports.setInt(1, postId);
                        psDeletePostReports.executeUpdate();
                    }

                    // 3d) delete comment reports
                    try (PreparedStatement psDeleteCommentReports = conn.prepareStatement(deleteCommentReportsSql)) {
                        psDeleteCommentReports.setInt(1, postId);
                        psDeleteCommentReports.executeUpdate();
                    }
                }

                // 4) Delete the post itself (comments removed via FK cascade or separately if you prefer)
                int affected;
                try (PreparedStatement psDeletePost = conn.prepareStatement(deletePostSql)) {
                    psDeletePost.setInt(1, postId);
                    affected = psDeletePost.executeUpdate();
                }

                conn.commit();
                return affected > 0;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int countReportedPosts() {
        String sql = """
    SELECT COUNT(DISTINCT p.postID) AS total
    FROM Posts p
    INNER JOIN Reports r ON p.postID = r.postID
    WHERE r.statusFlag = 'PENDING'
    """;


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

    // NEW: Count posts by user
    @Override
    public int countByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM Posts WHERE userID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }



}