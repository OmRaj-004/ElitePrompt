package com.eliteprompt.dao;

import com.eliteprompt.config.DBConnection;
import com.eliteprompt.model.Admin;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminDAO - all operations on the SEPARATE admins table.
 * Promoting a user copies their credentials into admins;
 * demoting deletes them from admins (they stay in users).
 */
public class AdminDAO {

    private Admin mapRow(ResultSet rs) throws Exception {
        Admin a = new Admin();
        a.setId(rs.getInt("id"));
        a.setFullName(rs.getString("full_name"));
        a.setUsername(rs.getString("username"));
        a.setEmail(rs.getString("email"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }

    /** Check credentials against the admins table only. */
    public Admin findMatching(String loginInput, String plainPassword) throws Exception {
        String sql = "SELECT * FROM admins WHERE username = ? OR email = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loginInput);
            ps.setString(2, loginInput);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && BCrypt.checkpw(plainPassword, rs.getString("password"))) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Admin> findAll() throws Exception {
        String sql = "SELECT * FROM admins ORDER BY id";
        List<Admin> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Demote: delete an account from the admins table (user stays in users). */
    public boolean remove(int adminId) throws Exception {
        String sql = "DELETE FROM admins WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            return ps.executeUpdate() == 1;
        }
    }

    /** Promote: copy a normal user's credentials into the admins table. */
    public boolean promote(int userId) throws Exception {
        String sql = "INSERT INTO admins (full_name, username, email, password) " +
                     "SELECT full_name, username, email, password FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() == 1;
        }
    }
}
