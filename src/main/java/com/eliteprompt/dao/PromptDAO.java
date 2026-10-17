package com.eliteprompt.dao;

import com.eliteprompt.config.DBConnection;
import com.eliteprompt.model.Prompt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * PromptDAO - CRUD for prompts table.
 * All queries use PreparedStatement + user_id check (ownership).
 */
public class PromptDAO {

    private Prompt mapRow(ResultSet rs) throws Exception {
        Prompt p = new Prompt();
        p.setId(rs.getInt("id"));
        p.setUserId(rs.getInt("user_id"));
        p.setTitle(rs.getString("title"));
        p.setCategory(rs.getString("category"));
        p.setRequirement(rs.getString("requirement"));
        p.setTone(rs.getString("tone"));
        p.setOutputFormat(rs.getString("output_format"));
        p.setGeneratedPrompt(rs.getString("generated_prompt"));
        p.setFavorite(rs.getBoolean("is_favorite"));
        p.setPublic(rs.getBoolean("is_public"));
        p.setTags(rs.getString("tags") == null ? "" : rs.getString("tags"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }

    public int countByUser(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM prompts WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countFavorites(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM prompts WHERE user_id = ? AND is_favorite = TRUE";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Prompt> recentByUser(int userId, int limit) throws Exception {
        String sql = "SELECT * FROM prompts WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Prompt> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // WHERE clause shared by count + list (search, category, tag, favorites-only)
    private String buildFilter(StringBuilder sql, List<Object> params,
                               int userId, String search, String category,
                               String tag, boolean favOnly) {
        sql.append("SELECT * FROM prompts WHERE user_id = ?");
        params.add(userId);

        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (favOnly) {
            sql.append(" AND is_favorite = TRUE");
        }
        if (tag != null && !tag.isEmpty()) {
            sql.append(" AND tags ILIKE ?");
            params.add("%" + tag + "%");
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (title ILIKE ? OR requirement ILIKE ? OR generated_prompt ILIKE ? OR tags ILIKE ?)");
            String like = "%" + search + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        return sql.toString();
    }

    private void setParams(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            Object v = params.get(i);
            if (v instanceof Integer) ps.setInt(i + 1, (Integer) v);
            else ps.setString(i + 1, (String) v);
        }
    }

    public int countFiltered(int userId, String search, String category, String tag, boolean favOnly) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM prompts WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (favOnly) {
            sql.append(" AND is_favorite = TRUE");
        }
        if (tag != null && !tag.isEmpty()) {
            sql.append(" AND tags ILIKE ?");
            params.add("%" + tag + "%");
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (title ILIKE ? OR requirement ILIKE ? OR generated_prompt ILIKE ? OR tags ILIKE ?)");
            String like = "%" + search + "%";
            params.add(like); params.add(like); params.add(like); params.add(like);
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // Search + category + tag + favorites filter with pagination
    public List<Prompt> findByUser(int userId, String search, String category,
                                   String tag, boolean favOnly, int limit, int offset) throws Exception {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        buildFilter(sql, params, userId, search, category, tag, favOnly);
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Prompt> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Old signature kept for backward compatibility (no pagination)
    public List<Prompt> findByUser(int userId, String search, String category) throws Exception {
        return findByUser(userId, search, category, "", false, 1000, 0);
    }

    public Prompt findById(int id, int userId) throws Exception {
        String sql = "SELECT * FROM prompts WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean insert(Prompt p) throws Exception {
        String sql = "INSERT INTO prompts (user_id, title, category, requirement, tone, output_format, generated_prompt, is_favorite, is_public, tags) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getUserId());
            ps.setString(2, p.getTitle());
            ps.setString(3, p.getCategory());
            ps.setString(4, p.getRequirement());
            ps.setString(5, p.getTone());
            ps.setString(6, p.getOutputFormat());
            ps.setString(7, p.getGeneratedPrompt());
            ps.setBoolean(8, p.isFavorite());
            ps.setBoolean(9, p.isPublic());
            ps.setString(10, p.getTags() == null ? "" : p.getTags());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean update(Prompt p) throws Exception {
        String sql = "UPDATE prompts SET title=?, category=?, requirement=?, tone=?, output_format=?, generated_prompt=?, is_public=?, tags=?, updated_at=CURRENT_TIMESTAMP WHERE id=? AND user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getCategory());
            ps.setString(3, p.getRequirement());
            ps.setString(4, p.getTone());
            ps.setString(5, p.getOutputFormat());
            ps.setString(6, p.getGeneratedPrompt());
            ps.setBoolean(7, p.isPublic());
            ps.setString(8, p.getTags() == null ? "" : p.getTags());
            ps.setInt(9, p.getId());
            ps.setInt(10, p.getUserId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int id, int userId) throws Exception {
        String sql = "DELETE FROM prompts WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    // Flip favorite flag (ownership checked)
    public boolean toggleFavorite(int id, int userId) throws Exception {
        String sql = "UPDATE prompts SET is_favorite = NOT is_favorite WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    // Flip public flag (ownership checked)
    public boolean togglePublic(int id, int userId) throws Exception {
        String sql = "UPDATE prompts SET is_public = NOT is_public WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    // ---- Public gallery (no login needed to view) ----

    public int countPublic(String search, String category) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM prompts p WHERE p.is_public = TRUE");
        List<Object> params = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            sql.append(" AND p.category = ?");
            params.add(category);
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (p.title ILIKE ? OR p.generated_prompt ILIKE ? OR p.tags ILIKE ?)");
            String like = "%" + search + "%";
            params.add(like); params.add(like); params.add(like);
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Prompt> findPublic(String search, String category, int limit, int offset) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, u.username AS author FROM prompts p JOIN users u ON u.id = p.user_id WHERE p.is_public = TRUE");
        List<Object> params = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            sql.append(" AND p.category = ?");
            params.add(category);
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (p.title ILIKE ? OR p.generated_prompt ILIKE ? OR p.tags ILIKE ?)");
            String like = "%" + search + "%";
            params.add(like); params.add(like); params.add(like);
        }
        sql.append(" ORDER BY p.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Prompt> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prompt p = mapRow(rs);
                    p.setAuthorName(rs.getString("author"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    // Copy a public prompt into the current user's library
    public boolean clonePrompt(int publicId, int userId) throws Exception {
        String sql = "INSERT INTO prompts (user_id, title, category, requirement, tone, output_format, generated_prompt, tags) " +
                     "SELECT ?, title, category, requirement, tone, output_format, generated_prompt, tags " +
                     "FROM prompts WHERE id = ? AND is_public = TRUE";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, publicId);
            return ps.executeUpdate() == 1;
        }
    }

    // All prompts of a user (for JSON export)
    public List<Prompt> findAllByUser(int userId) throws Exception {
        return findByUser(userId, "", "", "", false, 10000, 0);
    }
}
