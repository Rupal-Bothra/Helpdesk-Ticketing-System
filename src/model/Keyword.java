package model;

import java.sql.Timestamp;

/**
 * Keyword Model
 * Represents a keyword definition stored in the database for auto-priority assignment logic.
 */
public class Keyword {
    private int keywordId;
    private String keyword;
    private int weight;
    private String priorityLevel; // "HIGH", "MEDIUM", "LOW"
    private Timestamp createdAt;

    // Constructors
    public Keyword() {}

    public Keyword(int keywordId, String keyword, int weight, String priorityLevel, Timestamp createdAt) {
        this.keywordId = keywordId;
        this.keyword = keyword;
        this.weight = weight;
        this.priorityLevel = priorityLevel;
        this.createdAt = createdAt;
    }

    public Keyword(String keyword, int weight, String priorityLevel) {
        this.keyword = keyword;
        this.weight = weight;
        this.priorityLevel = priorityLevel;
    }

    // Getters and Setters
    public int getKeywordId() { return keywordId; }
    public void setKeywordId(int keywordId) { this.keywordId = keywordId; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Aliases for GUI compatibility
    public String getKeywordText() { return getKeyword(); }
    public void setKeywordText(String keyword) { setKeyword(keyword); }

    public int getWeightScore() { return getWeight(); }
    public void setWeightScore(int weight) { setWeight(weight); }

    public String getPriorityCategory() { return getPriorityLevel(); }
    public void setPriorityCategory(String priorityLevel) { setPriorityLevel(priorityLevel); }
}
