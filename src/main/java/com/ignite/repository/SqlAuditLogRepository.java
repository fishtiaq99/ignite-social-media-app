package com.ignite.repository;

import com.ignite.model.AuditLog;
import com.ignite.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlAuditLogRepository implements AuditLogRepository {

    @Override
    public List<AuditLog> findAll() {
        String sql = "SELECT logID, userID, adminID, actionType, " +
                "       targetEntity, targetID, timestamp " +
                "FROM AuditLog " +
                "ORDER BY timestamp DESC";

        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                logs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading audit log", e);
        }

        return logs;
    }

    @Override
    public List<AuditLog> findRecent(int limit) {
        // SQL Server: TOP <n> must be literal, so we safely concatenate the int
        String sql = "SELECT TOP " + limit + " logID, userID, adminID, actionType, " +
                "       targetEntity, targetID, timestamp " +
                "FROM AuditLog " +
                "ORDER BY timestamp DESC";

        List<AuditLog> logs = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                logs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading recent audit log", e);
        }

        return logs;
    }

    private AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getInt("logID"));

        int userId = rs.getInt("userID");
        log.setUserId(rs.wasNull() ? null : userId);

        int adminId = rs.getInt("adminID");
        log.setAdminId(rs.wasNull() ? null : adminId);

        log.setActionType(rs.getString("actionType"));
        log.setTargetEntity(rs.getString("targetEntity"));
        log.setTargetId(rs.getString("targetID"));
        log.setTimestamp(rs.getTimestamp("timestamp"));
        return log;
    }
}
