package com.ignite.repository;

import com.ignite.model.Inquiry;
import com.ignite.model.enums.InquiryStatus;
import com.ignite.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlInquiryRepository implements InquiryRepository {

    @Override
    public Inquiry save(Inquiry inquiry) {
        String sql = "INSERT INTO Inquiry (userID, message) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, inquiry.getUserId());
            stmt.setString(2, inquiry.getMessage());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        inquiry.setInquiryId(rs.getInt(1));
                        return inquiry;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving inquiry: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Optional<Inquiry> findById(int inquiryId) {
        String sql = "SELECT * FROM Inquiry WHERE inquiryID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inquiryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInquiry(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inquiry by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Inquiry> findByUserId(int userId) {
        List<Inquiry> inquiries = new ArrayList<>();
        String sql = "SELECT * FROM Inquiry WHERE userID = ? ORDER BY submitDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inquiries.add(mapResultSetToInquiry(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inquiries by user: " + e.getMessage(), e);
        }
        return inquiries;
    }

    @Override
    public List<Inquiry> findByStatus(InquiryStatus status) {
        List<Inquiry> inquiries = new ArrayList<>();
        String sql = "SELECT * FROM Inquiry WHERE statusFlag = ? ORDER BY submitDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, status == InquiryStatus.RESOLVED ? 1 : 0);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inquiries.add(mapResultSetToInquiry(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inquiries by status: " + e.getMessage(), e);
        }
        return inquiries;
    }

    @Override
    public boolean markAsResolved(int inquiryId) {
        String sql = "UPDATE Inquiry SET statusFlag = 1 WHERE inquiryID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inquiryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error marking inquiry as resolved: " + e.getMessage(), e);
        }
    }

    private Inquiry mapResultSetToInquiry(ResultSet rs) throws SQLException {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryId(rs.getInt("inquiryID"));
        inquiry.setUserId(rs.getInt("userID"));
        inquiry.setMessage(rs.getString("message"));
        inquiry.setSubmitDate(rs.getTimestamp("submitDate"));

        int statusFlag = rs.getInt("statusFlag");
        inquiry.setStatus(statusFlag == 1 ? InquiryStatus.RESOLVED : InquiryStatus.PENDING);

        return inquiry;
    }


    @Override
    public boolean update(Inquiry inquiry) {
        return false;
    }

    @Override
    public boolean delete(int inquiryId) {
        return false;
    }

    @Override
    public List<Inquiry> findPendingInquiries() {
        return findByStatus(InquiryStatus.PENDING);
    }

    @Override
    public List<Inquiry> findResolvedInquiries() {
        return findByStatus(InquiryStatus.RESOLVED);
    }

    @Override
    public int getInquiryCountByUser(int userId) {
        return 0;
    }

    @Override
    public List<Inquiry> findRecentInquiries(int limit) {
        return new ArrayList<>();
    }

    // NEW: Get all inquiries
    @Override
    public List<Inquiry> findAll() {
        List<Inquiry> inquiries = new ArrayList<>();
        String sql = "SELECT * FROM Inquiry ORDER BY submitDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inquiries.add(mapResultSetToInquiry(rs)); // you already have this mapper
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inquiries;
    }


    // NEW: Get unanswered inquiries
    @Override
    public List<Inquiry> findUnansweredInquiries() {
        List<Inquiry> inquiries = new ArrayList<>();
        String sql = "SELECT * FROM Inquiry WHERE statusFlag = 0 ORDER BY submitDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inquiries.add(mapResultSetToInquiry(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inquiries;
    }


    // NEW: Count all inquiries
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) AS total FROM Inquiry";

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


    // NEW: Count unanswered inquiries
    @Override
    public int countUnansweredInquiries() {
        String sql = "SELECT COUNT(*) AS total FROM Inquiry WHERE statusFlag = 0";

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

    @Override
    public boolean answerInquiry(int inquiryId, int adminId, String response) {
        String insertAnswerSql =
                "INSERT INTO Answers (adminID, inquiryID, adminResponse) VALUES (?, ?, ?)";
        String updateInquirySql =
                "UPDATE Inquiry SET statusFlag = 1 WHERE inquiryID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertAnswerSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateInquirySql)) {

                // 1) Insert admin's response into Answers
                insertStmt.setInt(1, adminId);
                insertStmt.setInt(2, inquiryId);
                insertStmt.setString(3, response);
                insertStmt.executeUpdate();

                // 2) Mark inquiry as resolved
                updateStmt.setInt(1, inquiryId);
                updateStmt.executeUpdate();

                conn.commit();
                return true;

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

}