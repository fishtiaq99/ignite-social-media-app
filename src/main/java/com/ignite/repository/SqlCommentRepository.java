package com.ignite.repository;

import com.ignite.model.Comment;
import com.ignite.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlCommentRepository implements CommentRepository {

    @Override
    public Comment save(Comment comment) {
        String sql = "INSERT INTO Comments (userID, postID, contentText) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, comment.getUserId());
            stmt.setInt(2, comment.getPostId());
            stmt.setString(3, comment.getContentText()); // Fix: use getContentText() instead of getContent()

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        comment.setCommentId(rs.getInt(1)); // Fix: setCommentId instead of setId
                        return comment;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving comment: " + e.getMessage(), e);
        }
        return null;
    }

    // Fix all the mapToComment calls to mapResultSetToComment
    @Override
    public Optional<Comment> findById(int commentId) {
        String sql = "SELECT * FROM Comments WHERE commentID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToComment(rs)); // Fix: mapResultSetToComment
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding comment by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Comment> findByPostId(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM Comments WHERE postID = ? ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs)); // Fix: mapResultSetToComment
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding comments by post: " + e.getMessage(), e);
        }
        return comments;
    }

    // Fix the mapping method name
    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(rs.getInt("commentID")); // Fix: setCommentId
        comment.setUserId(rs.getInt("userID"));
        comment.setPostId(rs.getInt("postID"));
        comment.setContentText(rs.getString("contentText")); // Fix: setContentText
        comment.setCreationDate(rs.getTimestamp("creationDate"));
        return comment;
    }

    // Implement other methods similarly...
    @Override
    public List<Comment> findByUserId(int userId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM Comments WHERE userID = ? ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding comments by user: " + e.getMessage(), e);
        }
        return comments;
    }

    @Override
    public boolean update(Comment comment) {
        String sql = "UPDATE Comments SET contentText = ? WHERE commentID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, comment.getContentText());
            stmt.setInt(2, comment.getCommentId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating comment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int commentId) {
        String sql = "DELETE FROM Comments WHERE commentID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting comment: " + e.getMessage(), e);
        }
    }

    @Override
    public int getCommentCountByPost(int postId) {
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
    public int getCommentCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM Comments WHERE userID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user comment count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<Comment> searchByContent(String query) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM Comments WHERE contentText LIKE ? ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching comments: " + e.getMessage(), e);
        }
        return comments;
    }

    @Override
    public boolean hasUserCommentedOnPost(int userId, int postId) {
        String sql = "SELECT TOP 1 commentID FROM Comments WHERE userID = ? AND postID = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if user has commented
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if user commented on post: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Comment> findRecentComments(int limit) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM Comments ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding recent comments: " + e.getMessage(), e);
        }

        return comments;
    }

    // NEW: Get all comments
    @Override
    public List<Comment> findAll() {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM Comments ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    // NEW: Get reported comments
    @Override
    public List<Comment> findReportedComments() {
        List<Comment> comments = new ArrayList<>();
        String sql = """
    SELECT c.* FROM Comments c 
    INNER JOIN Reports r ON c.commentID = r.commentID 
    WHERE r.statusFlag = 'PENDING'
    GROUP BY c.commentID, c.userID, c.postID, c.contentText, c.creationDate
    ORDER BY COUNT(r.reportID) DESC
    """;


        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    // NEW: Count all comments
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) AS total FROM Comments";

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


    // NEW: Admin delete comment
    @Override
    public boolean adminDeleteComment(int commentId) {
        String countReportsSql = "SELECT COUNT(*) FROM Reports WHERE commentID = ?";

        String insertResolvedSql = """
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
            'COMMENT'       AS contentType,
            commentID       AS contentID,
            reportDate,
            GETDATE()       AS resolvedDate,
            reason,
            reportID
        FROM Reports
        WHERE commentID = ?
        """;

        String deleteReportsSql = "DELETE FROM Reports WHERE commentID = ?";
        String deleteCommentSql = "DELETE FROM Comments WHERE commentID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1) Check if this comment has any reports
                int reportCount = 0;
                try (PreparedStatement psCount = conn.prepareStatement(countReportsSql)) {
                    psCount.setInt(1, commentId);
                    try (ResultSet rs = psCount.executeQuery()) {
                        if (rs.next()) {
                            reportCount = rs.getInt(1);
                        }
                    }
                }

                // 2) If there ARE reports, archive them & delete from Reports
                if (reportCount > 0) {
                    // 2a) archive into ResolvedReports
                    try (PreparedStatement psInsertResolved = conn.prepareStatement(insertResolvedSql)) {
                        psInsertResolved.setInt(1, commentId);
                        psInsertResolved.executeUpdate();
                    }

                    // 2b) delete from Reports to avoid FK issues
                    try (PreparedStatement psDeleteReports = conn.prepareStatement(deleteReportsSql)) {
                        psDeleteReports.setInt(1, commentId);
                        psDeleteReports.executeUpdate();
                    }
                }

                // 3) Always delete the actual comment
                int affected;
                try (PreparedStatement psDeleteComment = conn.prepareStatement(deleteCommentSql)) {
                    psDeleteComment.setInt(1, commentId);
                    affected = psDeleteComment.executeUpdate();
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
    public int countReportedComments() {
        String sql = """
    SELECT COUNT(DISTINCT c.commentID) AS total 
    FROM Comments c 
    INNER JOIN Reports r ON c.commentID = r.commentID 
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

    // NEW: Count comments by user
    @Override
    public int countByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM Comments WHERE userID = ?";

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

    // NEW: Count comments by post
    @Override
    public int countByPost(int postId) {
        String sql = "SELECT COUNT(*) AS total FROM Comments WHERE postID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
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