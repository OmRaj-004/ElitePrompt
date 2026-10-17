package com.eliteprompt.dao;

import com.eliteprompt.config.DBConnection;
import com.eliteprompt.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * UserDAO - all user database operations.
 * Uses PreparedStatement (safe from SQL injection).
 */
public class UserDAO {

    // Count all users (admin dashboard stat)
    public int countAll() throws Exception {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Count how many prompts a user has (admin dashboard stat)
    public int countPromptsOf(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM prompts WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // List all users for the admin dashboard
    public java.util.List<User> findAll() throws Exception {
        String sql = "SELECT * FROM users ORDER BY id";
        java.util.List<User> list = new java.util.ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                list.add(u);
            }
        }
        return list;
    }

    // Remove a user: their prompts and feedback are removed by ON DELETE CASCADE
    public boolean remove(int userId) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() == 1;
        }
    }

    // Check if a user with this id exists (used before promoting to admin)
    public boolean existsById(int userId) throws Exception {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Check if username or email already exists
    public boolean exists(String username, String email) throws Exception {
        String sql = "SELECT id FROM users WHERE username = ? OR email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Register new user (hash password with BCrypt)
    public boolean register(String fullName, String username, String email, String plainPassword) throws Exception {
        String sql = "INSERT INTO users (full_name, username, email, password) VALUES (?, ?, ?, ?)";
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, hash);
            return ps.executeUpdate() == 1;
        }
    }

    // Login with username OR email + password (BCrypt check)
    // Returns User if OK, null if invalid.
    public User login(String loginInput, String plainPassword) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loginInput);
            ps.setString(2, loginInput);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password");
                    if (BCrypt.checkpw(plainPassword, hash)) {
                        User u = new User();
                        u.setId(rs.getInt("id"));
                        u.setFullName(rs.getString("full_name"));
                        u.setUsername(rs.getString("username"));
                        u.setEmail(rs.getString("email"));
                        u.setPassword(hash);
                        return u;
                    }
                }
            }
        }
        return null;
    }

    // Save the user's own OpenCode Zen API key (never hardcoded)
    public boolean saveZenApiKey(int userId, String apiKey) throws Exception {
        String sql = "UPDATE users SET zen_api_key = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, apiKey == null ? "" : apiKey.trim());
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    // Load the user's saved Zen API key ("" if none saved yet)
    public String getZenApiKey(int userId) throws Exception {
        String sql = "SELECT zen_api_key FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String key = rs.getString("zen_api_key");
                    return key == null ? "" : key;
                }
            }
        }
        return "";
    }
}
