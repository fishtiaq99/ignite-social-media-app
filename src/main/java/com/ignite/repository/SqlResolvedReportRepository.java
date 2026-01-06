package com.ignite.repository;

import com.ignite.model.ResolvedReport;
import com.ignite.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlResolvedReportRepository {

    // ----------------------------------------------------------
    // INSERT a resolved report
    // ----------------------------------------------------------
    public boolean insertResolvedReport(ResolvedReport rr) {
        String sql = """
                INSERT INTO ResolvedReports
                (reporterUserID, contentType, contentID, reportDate, resolvedDate, reason, reportID)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rr.getReporterUserId());
            stmt.setString(2, rr.getContentType());
            stmt.setInt(3, rr.getContentId());
            stmt.setTimestamp(4, new Timestamp(rr.getReportDate().getTime()));
            stmt.setTimestamp(5, new Timestamp(rr.getResolvedDate().getTime()));
            stmt.setString(6, rr.getReason());
            if (rr.getReportId() != null) {
                stmt.setInt(7, rr.getReportId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------------------------------------------------------
    // SELECT all resolved reports
    // ----------------------------------------------------------
    public List<ResolvedReport> findAll() {
        List<ResolvedReport> list = new ArrayList<>();

        String sql = """
                SELECT resolvedID, reporterUserID, contentType, contentID,
                       reportDate, resolvedDate, reason, reportID
                FROM ResolvedReports
                ORDER BY resolvedDate DESC
                """;

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ResolvedReport rr = new ResolvedReport();

                rr.setResolvedId(rs.getInt("resolvedID"));
                rr.setReporterUserId(rs.getInt("reporterUserID"));
                rr.setContentType(rs.getString("contentType"));
                rr.setContentId(rs.getInt("contentID"));

                Timestamp reportTS = rs.getTimestamp("reportDate");
                if (reportTS != null) rr.setReportDate(new java.util.Date(reportTS.getTime()));

                Timestamp resolvedTS = rs.getTimestamp("resolvedDate");
                if (resolvedTS != null) rr.setResolvedDate(new java.util.Date(resolvedTS.getTime()));

                rr.setReason(rs.getString("reason"));

                int reportIdVal = rs.getInt("reportID");
                rr.setReportId(rs.wasNull() ? null : reportIdVal);

                list.add(rr);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ----------------------------------------------------------
    // Helper: Insert directly from parameters (optional)
    // ----------------------------------------------------------
    public boolean resolve(int reporterUserId,
                           String contentType,
                           int contentId,
                           java.util.Date reportDate,
                           String reason,
                           Integer reportId) {

        ResolvedReport rr = new ResolvedReport();
        rr.setReporterUserId(reporterUserId);
        rr.setContentType(contentType);
        rr.setContentId(contentId);
        rr.setReportDate(reportDate);
        rr.setResolvedDate(new java.util.Date()); // now
        rr.setReason(reason);
        rr.setReportId(reportId);

        return insertResolvedReport(rr);
    }
}
