package com.ignite.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;" +
                    "databaseName=IGNITE;" +
                    "encrypt=true;" +
                    "trustServerCertificate=true";

    private static final String USER = "sa";
    private static final String PASSWORD = "JavaApp123!";

    // Singleton instance (volatile for thread safety)
    private static volatile DatabaseUtil instance;

    // Shared connection
    private Connection connection;

    // Private constructor
    private DatabaseUtil() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to initialize DatabaseUtil", e);
        }
    }

    // Thread-safe Singleton getter
    public static DatabaseUtil getInstance() {
        if (instance == null) {
            synchronized (DatabaseUtil.class) {
                if (instance == null) {
                    instance = new DatabaseUtil();
                }
            }
        }
        return instance;
    }

    // Get shared connection, recreate if closed
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
        return connection;
    }

    // Optional connection test
    public void testConnection() {
        try {
            Connection conn = getConnection();
            System.out.println("✅ Database connection successful (Singleton DatabaseUtil)!");
        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
        }
    }
}
