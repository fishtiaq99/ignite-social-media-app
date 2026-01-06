package com.ignite.repository;

import com.ignite.model.enums.AlertType;
import com.ignite.model.SystemAlert;

import com.ignite.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlAlertRepository implements AlertRepository {


    private com.ignite.model.SystemAlert mapResultSetToAlert(ResultSet rs) throws SQLException {
        com.ignite.model.SystemAlert alert = new com.ignite.model.SystemAlert();
        alert.setAlertId(rs.getInt("alertID"));
        alert.setAdminId(rs.getInt("adminID"));
        alert.setMessage(rs.getString("message"));
        alert.setCreationDate(rs.getTimestamp("creationDate"));

        String alertType = rs.getString("alertType");
        alert.setAlertType(AlertType.valueOf(alertType));

        alert.setActive(rs.getBoolean("isActive"));
        return alert;
    }



    @Override
    public SystemAlert save(SystemAlert alert) {
        String sql = "INSERT INTO Alerts (adminID, message, alertType, isActive) " +
                "VALUES (?, ?, ?, 1)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, alert.getAdminId());
            stmt.setString(2, alert.getMessage());
            stmt.setString(3, alert.getAlertType().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        alert.setAlertId(rs.getInt(1));
                        return alert;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving alert: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Optional<SystemAlert> findById(int alertId) {
        String sql = "SELECT * FROM Alerts WHERE alertID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alertId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding alert by id: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<SystemAlert> findAll() {
        List<SystemAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all alerts: " + e.getMessage(), e);
        }

        return alerts;
    }

    @Override
    public boolean update(SystemAlert alert) {
        String sql = "UPDATE Alerts SET message = ?, alertType = ?, isActive = ? WHERE alertID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, alert.getMessage());
            stmt.setString(2, alert.getAlertType().name());
            stmt.setBoolean(3, alert.isActive());
            stmt.setInt(4, alert.getAlertId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating alert: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int alertId) {
        String sql = "DELETE FROM Alerts WHERE alertID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alertId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting alert: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SystemAlert> findActiveAlerts() {
        List<SystemAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts WHERE isActive = 1 ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active alerts: " + e.getMessage(), e);
        }
        return alerts;
    }

    @Override
    public List<SystemAlert> findInactiveAlerts() {
        List<SystemAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts WHERE isActive = 0 ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inactive alerts: " + e.getMessage(), e);
        }
        return alerts;
    }

    @Override
    public boolean deactivateAlert(int alertId) {
        String sql = "UPDATE Alerts SET isActive = 0 WHERE alertID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alertId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating alert: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activateAlert(int alertId) {
        String sql = "UPDATE Alerts SET isActive = 1 WHERE alertID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alertId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error activating alert: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SystemAlert> findByType(AlertType alertType) {
        List<SystemAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts WHERE alertType = ? ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, alertType.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding alerts by type: " + e.getMessage(), e);
        }
        return alerts;
    }

    @Override
    public List<SystemAlert> findUrgentAlerts() {
        List<SystemAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts " +
                "WHERE alertType IN ('URGENT', 'SAFETY') AND isActive = 1 " +
                "ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding urgent alerts: " + e.getMessage(), e);
        }
        return alerts;
    }

    @Override
    public List<SystemAlert> findByAdminId(int adminId) {
        List<SystemAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts WHERE adminID = ? ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adminId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding alerts by adminId: " + e.getMessage(), e);
        }

        return alerts;
    }

    @Override
    public int getAlertCountByAdmin(int adminId) {
        String sql = "SELECT COUNT(*) AS cnt FROM Alerts WHERE adminID = ?";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adminId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting alerts by admin: " + e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public List<SystemAlert> findRecentAlerts(int limit) {
        List<SystemAlert> alerts = new ArrayList<>();

        // SQL Server style TOP (since you're using IDENTITY + GETDATE)
        String sql = "SELECT TOP " + limit + " * FROM Alerts ORDER BY creationDate DESC";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding recent alerts: " + e.getMessage(), e);
        }

        return alerts;
    }

    private SystemAlert mapRow(ResultSet rs) throws SQLException {
        SystemAlert alert = new SystemAlert();
        alert.setAlertId(rs.getInt("alertID"));
        alert.setAdminId(rs.getInt("adminID"));
        alert.setMessage(rs.getString("message"));
        alert.setCreationDate(rs.getTimestamp("creationDate"));

        String typeStr = rs.getString("alertType");
        if (typeStr != null) {
            alert.setAlertType(AlertType.valueOf(typeStr));
        }

        alert.setActive(rs.getBoolean("isActive"));
        return alert;
    }
}