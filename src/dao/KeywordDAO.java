package dao;

import db.DBConnection;
import model.Keyword;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * KeywordDAO
 * Data Access Object for managing keyword weight & priority mapping rules stored in MySQL.
 */
public class KeywordDAO {

    /**
     * Fetches all keywords from database.
     * @return List of Keyword objects
     */
    public List<Keyword> getAllKeywords() {
        List<Keyword> list = new ArrayList<>();
        String sql = "SELECT keyword_id, keyword, weight, priority_level, created_at FROM keywords ORDER BY weight DESC, keyword ASC";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Keyword(
                    rs.getInt("keyword_id"),
                    rs.getString("keyword"),
                    rs.getInt("weight"),
                    rs.getString("priority_level"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("KeywordDAO.getAllKeywords Error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Adds a new keyword mapping rule.
     * @param keyword Keyword object to add
     * @return true if successful
     */
    public boolean addKeyword(Keyword keyword) {
        String sql = "INSERT INTO keywords (keyword, weight, priority_level) VALUES (?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keyword.getKeyword().trim().toLowerCase());
            stmt.setInt(2, keyword.getWeight());
            stmt.setString(3, keyword.getPriorityLevel());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("KeywordDAO.addKeyword Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates weight and priority of an existing keyword rule.
     * @param keyword Keyword object with updated values
     * @return true if updated
     */
    public boolean updateKeyword(Keyword keyword) {
        String sql = "UPDATE keywords SET keyword = ?, weight = ?, priority_level = ? WHERE keyword_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keyword.getKeyword().trim().toLowerCase());
            stmt.setInt(2, keyword.getWeight());
            stmt.setString(3, keyword.getPriorityLevel());
            stmt.setInt(4, keyword.getKeywordId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("KeywordDAO.updateKeyword Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a keyword mapping by ID.
     * @param keywordId Keyword ID to remove
     * @return true if deleted
     */
    public boolean deleteKeyword(int keywordId) {
        String sql = "DELETE FROM keywords WHERE keyword_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, keywordId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("KeywordDAO.deleteKeyword Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
