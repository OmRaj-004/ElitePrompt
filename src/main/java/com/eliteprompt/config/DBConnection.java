package com.eliteprompt.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection - Elite Prompt
 * TYBCA Mini Project - Simple JDBC connection
 *
 * Change these values to match your PostgreSQL setup.
 */
public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/eliteprompt";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a new database connection.
     * Caller must close it (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
