package com.ignite.repository;

import com.ignite.model.Report;
import com.ignite.model.enums.ReportStatus;
import com.ignite.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlReportRepository implements ReportRepository {

    @Override
    public Report save(Report report) {
        String sql = "INSERT INTO Reports (reporterUserID, postID, commentID, reason) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, report.getReporterUserId());

            if (report.getPostId() != null) {
                stmt.setInt(2, report.getPostId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (report.getCommentId() != null) {
                stmt.setInt(3, report.getCommentId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, report.getReason());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        report.setReportId(rs.getInt(1));
                        return report;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving report: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Optional<Report> findById(int reportId) {
        String sql = "SELECT * FROM Reports WHERE reportID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding report by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Report> findByStatus(ReportStatus status) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Reports WHERE statusFlag = ? ORDER BY reportDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reports by status: " + e.getMessage(), e);
        }
        return reports;
    }

    @Override
    public List<Report> findPendingReports() {
        return findByStatus(ReportStatus.PENDING);
    }

    @Override
    public List<Report> findResolvedReports() {
        return findByStatus(ReportStatus.RESOLVED);
    }

    @Override
    public boolean updateStatus(int reportId, ReportStatus status) {
        String sql = "UPDATE Reports SET statusFlag = ? WHERE reportID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, reportId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating report status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Report> findByPostId(int postId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Reports WHERE postID = ? ORDER BY reportDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reports by post: " + e.getMessage(), e);
        }
        return reports;
    }

    @Override
    public List<Report> findByCommentId(int commentId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Reports WHERE commentID = ? ORDER BY reportDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reports by comment: " + e.getMessage(), e);
        }
        return reports;
    }

    @Override
    public List<Report> findByReporterId(int reporterId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Reports WHERE reporterUserID = ? ORDER BY reportDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reporterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reports by reporter: " + e.getMessage(), e);
        }
        return reports;
    }

    @Override
    public List<Report> findAll() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Reports ORDER BY reportDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all reports: " + e.getMessage(), e);
        }
        return reports;
    }

    @Override
    public boolean update(Report report) {
        String sql = "UPDATE Reports SET reason = ?, statusFlag = ? WHERE reportID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, report.getReason());
            stmt.setString(2, report.getStatus().name());
            stmt.setInt(3, report.getReportId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating report: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int reportId) {
        String sql = "DELETE FROM Reports WHERE reportID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting report: " + e.getMessage(), e);
        }
    }

    @Override
    public int getReportCountByStatus(ReportStatus status) {
        String sql = "SELECT COUNT(*) FROM Reports WHERE statusFlag = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting report count: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<Report> findRecentReports(int limit) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM Reports ORDER BY reportDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding recent reports: " + e.getMessage(), e);
        }
        return reports;
    }

    @Override
    public boolean hasUserReportedContent(int userId, Integer postId, Integer commentId) {
        String sql = "SELECT 1 FROM Reports WHERE reporterUserID = ? AND ((postID = ?) OR (commentID = ?))";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setObject(2, postId);
            stmt.setObject(3, commentId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if user reported content: " + e.getMessage(), e);
        }
    }

    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setReportId(rs.getInt("reportID"));
        report.setReporterUserId(rs.getInt("reporterUserID"));

        Integer postId = rs.getObject("postID", Integer.class);
        report.setPostId(postId);

        Integer commentId = rs.getObject("commentID", Integer.class);
        report.setCommentId(commentId);

        report.setReason(rs.getString("reason"));
        report.setReportDate(rs.getTimestamp("reportDate"));

        String status = rs.getString("statusFlag");
        report.setStatus(ReportStatus.valueOf(status));

        return report;
    }

    @Override
    public int countPendingReports() {
        String sql = "SELECT COUNT(*) AS total FROM Reports WHERE statusFlag = 'PENDING'";

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