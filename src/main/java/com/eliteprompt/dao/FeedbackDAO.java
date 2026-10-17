package com.eliteprompt.dao;

import com.eliteprompt.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

/** FeedbackDAO - saves contact/feedback messages. */
public class FeedbackDAO {

    public boolean save(String name, String email, String subject, String message) throws Exception {
        String sql = "INSERT INTO feedback (name, email, subject, message) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, subject);
            ps.setString(4, message);
            return ps.executeUpdate() == 1;
        }
    }
}
