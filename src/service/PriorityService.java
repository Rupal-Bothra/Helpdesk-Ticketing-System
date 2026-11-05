package service;

import dao.KeywordDAO;
import model.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * PriorityService
 * Core Business Logic Engine for Automatic Priority Assignment.
 *
 * Adjusted Scoring Logic (Bug 1 Fix):
 * 1. Fetch keyword-weight rules dynamically from MySQL database via KeywordDAO.
 *    - HIGH-category keywords  ==> Weight = 5 (e.g. "urgent", "server down", "critical", "crash")
 *    - MEDIUM-category keywords ==> Weight = 3 (e.g. "slow", "error", "not working", "bug")
 *    - LOW-category keywords    ==> Weight = 1 (e.g. "request", "installation", "query", "help")
 *
 * 2. Convert incoming complaint description to lowercase for case-insensitive matching.
 * 3. Scan description text for matches against database keywords and sum integer weights.
 *
 * 4. Evaluation Thresholds:
 *    - HIGH: Total Score >= 8 OR presence of explicit HIGH-category keyword (weight >= 5).
 *    - MEDIUM: Total Score >= 3 (e.g. single MEDIUM keyword or multiple MEDIUM keywords).
 *    - LOW: Total Score < 3 (Multiple LOW keywords like "installation" (1) + "request" (1) = 2, staying LOW).
 */
public class PriorityService {

    private final KeywordDAO keywordDAO;

    public PriorityService() {
        this.keywordDAO = new KeywordDAO();
    }

    /**
     * Inner class to hold the result of priority calculation, including detailed score breakdown.
     */
    public static class PriorityResult {
        private final String priority;
        private final int score;
        private final List<String> matchedKeywords;

        public PriorityResult(String priority, int score, List<String> matchedKeywords) {
            this.priority = priority;
            this.score = score;
            this.matchedKeywords = matchedKeywords;
        }

        public String getPriority() { return priority; }
        public int getScore() { return score; }
        public List<String> getMatchedKeywords() { return matchedKeywords; }
    }

    /**
     * Calculates ticket priority based on complaint description scanning against database keywords.
     * 
     * @param description Complaint description entered by user
     * @return Priority string: "HIGH", "MEDIUM", or "LOW"
     */
    public String calculatePriority(String description) {
        return calculatePriorityWithDetails(description).getPriority();
    }

    /**
     * Alias for calculatePriority.
     */
    public String assignPriority(String description) {
        return calculatePriority(description);
    }

    /**
     * Calculates priority and provides diagnostic details (matched keywords and total score).
     * 
     * @param description Complaint description text
     * @return PriorityResult object containing priority, total score, and matched keywords list
     */
    public PriorityResult calculatePriorityWithDetails(String description) {
        if (description == null || description.trim().isEmpty()) {
            return new PriorityResult("LOW", 0, new ArrayList<>());
        }

        // Normalize text for case-insensitive match
        String normalizedText = description.toLowerCase();
        
        // Fetch dynamic keyword rules from MySQL database
        List<Keyword> keywordsList = keywordDAO.getAllKeywords();

        int totalScore = 0;
        List<String> matchedKeywords = new ArrayList<>();
        boolean hasHighCategoryKeyword = false;

        /*
         * KEYWORD SCORING LOOP:
         * Iterate through all database keywords and calculate accumulated score.
         */
        for (Keyword k : keywordsList) {
            String kw = k.getKeyword().toLowerCase().trim();
            if (kw.isEmpty()) continue;

            // Check if the complaint description contains the keyword phrase
            if (normalizedText.contains(kw)) {
                totalScore += k.getWeight();
                matchedKeywords.add(k.getKeyword() + " (weight: " + k.getWeight() + ")");

                // Flag critical HIGH-category keywords (weight >= 5)
                if ("HIGH".equalsIgnoreCase(k.getPriorityLevel()) || k.getWeight() >= 5) {
                    hasHighCategoryKeyword = true;
                }
            }
        }

        /*
         * ADJUSTED SCORE THRESHOLD EVALUATION:
         * - Score >= 8 OR presence of explicit HIGH category keyword ==> HIGH
         * - Score >= 3 ==> MEDIUM
         * - Score < 3 ==> LOW (ensures multiple LOW keywords like "installation"(1)+"request"(1)=2 remain LOW)
         */
        String finalPriority;
        if (hasHighCategoryKeyword || totalScore >= 8) {
            finalPriority = "HIGH";
        } else if (totalScore >= 3) {
            finalPriority = "MEDIUM";
        } else {
            finalPriority = "LOW";
        }

        System.out.println("[PriorityService] Evaluated text. Matched: " + matchedKeywords + 
                           ", Total Score: " + totalScore + " => Assigned Priority: " + finalPriority);

        return new PriorityResult(finalPriority, totalScore, matchedKeywords);
    }
}
