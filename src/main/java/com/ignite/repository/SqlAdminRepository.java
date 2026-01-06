package com.ignite.repository;

import com.ignite.model.Admin;
import com.ignite.util.DatabaseUtil;

import java.sql.*;

public class SqlAdminRepository implements AdminRepository {

    @Override
    public Admin findByUsername(String username) {
        String sql = "SELECT * FROM Admin WHERE username = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("adminID"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                admin.setEmail(rs.getString("email"));
                return admin;
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding admin: " + e.getMessage(), e);
        }
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getInt("adminID"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));  // real field
        admin.setEmail(rs.getString("email"));        // real field
        return admin;
    }



}
