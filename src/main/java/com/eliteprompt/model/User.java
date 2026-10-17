package com.eliteprompt.model;

import java.sql.Timestamp;

/** Simple User bean (maps to the users table). */
public class User {
    private int id;
    private String fullName;
    private String username;
    private String email;
    private String password; // hashed
    private int promptCount; // transient: filled by AdminServlet for the table
    private String zenApiKey; // OpenCode Zen API key (user's own, optional)
    private Timestamp createdAt;

    // Getters and setters (kept simple for viva)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getPromptCount() { return promptCount; }
    public void setPromptCount(int promptCount) { this.promptCount = promptCount; }

    public String getZenApiKey() { return zenApiKey; }
    public void setZenApiKey(String zenApiKey) { this.zenApiKey = zenApiKey; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
