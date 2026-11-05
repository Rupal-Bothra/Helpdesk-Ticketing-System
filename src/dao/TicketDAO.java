package dao;

import db.DBConnection;
import model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TicketDAO
 * Data Access Object for tickets table. Provides CRUD, search, filtering, and analytics query capabilities.
 */
public class TicketDAO {

    /**
     * Inserts a new ticket into database.
     * @param ticket Ticket entity
     * @return true if inserted successfully
     */
    public boolean createTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (user_id, title, description, category, priority, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticket.getUserId());
            stmt.setString(2, ticket.getTitle().trim());
            stmt.setString(3, ticket.getDescription().trim());
            stmt.setString(4, ticket.getCategory() != null ? ticket.getCategory() : "General");
            stmt.setString(5, ticket.getPriority());
            stmt.setString(6, ticket.getStatus() != null ? ticket.getStatus() : "OPEN");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TicketDAO.createTicket Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fetches a single ticket by its ticket ID.
     * @param ticketId ID of the ticket
     * @return Ticket object or null if not found
     */
    public Ticket getTicketById(int ticketId) {
        String sql = "SELECT t.ticket_id, t.user_id, u.name AS user_name, t.title, t.description, t.category, " +
                     "t.priority, t.status, t.admin_remark, t.created_at, t.updated_at " +
                     "FROM tickets t JOIN users u ON t.user_id = u.user_id WHERE t.ticket_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs, true);
                }
            }
        } catch (SQLException e) {
            System.err.println("TicketDAO.getTicketById Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetches all tickets submitted by a specific employee.
     * @param userId ID of the employee
     * @return List of tickets owned by the employee
     */
    public List<Ticket> getTicketsByUserId(int userId) {
        List<Ticket> list = new ArrayList<>();
        String sql = "SELECT ticket_id, user_id, title, description, category, priority, status, admin_remark, created_at, updated_at " +
                     "FROM tickets WHERE user_id = ? ORDER BY created_at DESC";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTicket(rs, false));
                }
            }
        } catch (SQLException e) {
            System.err.println("TicketDAO.getTicketsByUserId Error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Fetches all tickets with optional filtering by priority, status, and keyword search query (for Admin View).
     * @param priorityFilter "ALL" or specific priority ("HIGH", "MEDIUM", "LOW")
     * @param statusFilter "ALL" or specific status ("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED")
     * @param searchQuery Search term for title, description, or employee name
     * @return List of matching tickets
     */
    public List<Ticket> getAllTickets(String priorityFilter, String statusFilter, String searchQuery) {
        List<Ticket> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT t.ticket_id, t.user_id, u.name AS user_name, t.title, t.description, t.category, " +
            "t.priority, t.status, t.admin_remark, t.created_at, t.updated_at " +
            "FROM tickets t JOIN users u ON t.user_id = u.user_id WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (priorityFilter != null && !priorityFilter.equalsIgnoreCase("ALL")) {
            sql.append(" AND t.priority = ?");
            params.add(priorityFilter);
        }

        if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
            sql.append(" AND t.status = ?");
            params.add(statusFilter);
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append(" AND (LOWER(t.title) LIKE ? OR LOWER(t.description) LIKE ? OR LOWER(u.name) LIKE ?)");
            String term = "%" + searchQuery.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        sql.append(" ORDER BY t.created_at DESC");

        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTicket(rs, true));
                }
            }
        } catch (SQLException e) {
            System.err.println("TicketDAO.getAllTickets Error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates status and admin remarks for a specific ticket.
     * @param ticketId Ticket ID
     * @param newStatus New status value
     * @param adminRemark Resolution remarks or notes from admin
     * @return true if updated
     */
    public boolean updateTicketStatus(int ticketId, String newStatus, String adminRemark) {
        String sql = "UPDATE tickets SET status = ?, admin_remark = ? WHERE ticket_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, adminRemark != null ? adminRemark.trim() : "");
            stmt.setInt(3, ticketId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TicketDAO.updateTicketStatus Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Aggregate ticket counts by Priority level (for Analytics panel).
     * @return Map of Priority -> Count
     */
    public Map<String, Integer> getTicketCountsByPriority() {
        Map<String, Integer> map = new HashMap<>();
        map.put("HIGH", 0);
        map.put("MEDIUM", 0);
        map.put("LOW", 0);

        String sql = "SELECT priority, COUNT(*) AS cnt FROM tickets GROUP BY priority";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return map;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("priority").toUpperCase(), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Aggregate ticket counts by Status (for Analytics panel).
     * @return Map of Status -> Count
     */
    public Map<String, Integer> getTicketCountsByStatus() {
        Map<String, Integer> map = new HashMap<>();
        map.put("OPEN", 0);
        map.put("IN_PROGRESS", 0);
        map.put("RESOLVED", 0);
        map.put("CLOSED", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM tickets GROUP BY status";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return map;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("status").toUpperCase(), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Helper to map ResultSet rows to Ticket model.
     */
    private Ticket mapResultSetToTicket(ResultSet rs, boolean includesUser) throws SQLException {
        Ticket ticket = new Ticket(
            rs.getInt("ticket_id"),
            rs.getInt("user_id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getString("priority"),
            rs.getString("status"),
            rs.getString("admin_remark"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
        if (includesUser) {
            ticket.setUserName(rs.getString("user_name"));
        }
        return ticket;
    }
}
