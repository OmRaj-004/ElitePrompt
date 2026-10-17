package com.eliteprompt.model;

import java.sql.Timestamp;

/**
 * Admin bean - lives in the SEPARATE admins table (security).
 * Admin credentials are never stored in the users table, so a
 * users-table breach cannot expose admin passwords.
 */
public class Admin {
    private int id;
    private String fullName;
    private String username;
    private String email;
    private Timestamp createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
