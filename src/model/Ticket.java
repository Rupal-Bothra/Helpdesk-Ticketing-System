package model;

import java.sql.Timestamp;

/**
 * Ticket Model
 * Represents a complaint/ticket raised by an employee.
 */
public class Ticket {
    private int ticketId;
    private int userId;
    private String userName; // Joined field for Admin view
    private String title;
    private String description;
    private String category;
    private String priority; // "HIGH", "MEDIUM", "LOW"
    private String status;   // "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"
    private String adminRemark;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors
    public Ticket() {}

    public Ticket(int ticketId, int userId, String title, String description, String category, 
                  String priority, String status, String adminRemark, Timestamp createdAt, Timestamp updatedAt) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.adminRemark = adminRemark;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminRemark() { return adminRemark; }
    public void setAdminRemark(String adminRemark) { this.adminRemark = adminRemark; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
