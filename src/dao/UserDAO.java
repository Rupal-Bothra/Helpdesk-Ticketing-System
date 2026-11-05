package dao;

import db.DBConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserDAO
 * Data Access Object for managing User records in MySQL database.
 * Uses PreparedStatement for security against SQL injection.
 */
public class UserDAO {

    /**
     * Authenticates a user by email and password.
     * @param email User's email
     * @param password User's password
     * @return User object if authentication succeeds, null otherwise
     */
    public User authenticate(String email, String password) {
        String sql = "SELECT user_id, name, email, password, role, created_at FROM users WHERE LOWER(email) = LOWER(?) AND password = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            stmt.setString(2, password.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.authenticate Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registers a new user (Employee role by default).
     * @param user User object containing registration details
     * @return true if insertion succeeds, false otherwise
     */
    public boolean register(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName().trim());
            stmt.setString(2, user.getEmail().trim().toLowerCase());
            stmt.setString(3, user.getPassword().trim());
            stmt.setString(4, user.getRole() != null ? user.getRole() : "EMPLOYEE");

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.register Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if an email address is already registered.
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE LOWER(email) = LOWER(?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.emailExists Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
