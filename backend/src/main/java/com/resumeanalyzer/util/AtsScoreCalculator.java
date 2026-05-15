package com.resumeanalyzer.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core ATS scoring engine.
 *
 * Scoring breakdown:
 *   Keywords Match      → 40 points
 *   Section Completeness→ 25 points
 *   Technical Skills    → 20 points
 *   Formatting Quality  → 15 points
 *   Total               → 100 points
 */
@Component
public class AtsScoreCalculator {

    // Industry-standard keywords for Software Engineering / Java Backend roles
    private static final List<String> CORE_KEYWORDS = Arrays.asList(
        "java", "spring boot", "spring", "rest api", "restful", "microservices",
        "mysql", "sql", "postgresql", "mongodb", "git", "github", "maven", "gradle",
        "docker", "kubernetes", "aws", "azure", "gcp", "react", "angular", "vue",
        "javascript", "typescript", "html", "css", "python", "node.js",
        "data structures", "algorithms", "object oriented", "oop", "design patterns",
        "junit", "mockito", "testing", "hibernate", "jpa", "jdbc",
        "api", "backend", "frontend", "full stack", "agile", "scrum",
        "ci/cd", "jenkins", "linux", "json", "xml", "jwt", "oauth",
        "authentication", "authorization", "security", "redis", "kafka",
        "multithreading", "concurrency", "collections", "lambda", "streams"
    );

    // Required resume sections
    private static final List<String> REQUIRED_SECTIONS = Arrays.asList(
        "experience", "education", "skills", "projects",
        "summary", "objective", "certifications", "achievements",
        "contact", "email", "phone"
    );

    // High-value technical skills with individual weights
    private static final Map<String, Integer> SKILL_WEIGHTS = new LinkedHashMap<>() {{
        put("java",         5);
        put("spring boot",  5);
        put("spring",       4);
        put("mysql",        4);
        put("sql",          3);
        put("git",          3);
        put("rest api",     3);
        put("docker",       3);
        put("aws",          3);
        put("react",        3);
        put("python",       2);
        put("javascript",   2);
        put("junit",        2);
        put("hibernate",    2);
        put("microservices",2);
    }};

    /**
     * Main method: calculates full ATS score for a resume text.
     * Returns a map with all score components and keyword lists.
     */
    public Map<String, Object> calculateScore(String resumeText) {
        if (resumeText == null || resumeText.isBlank()) {
            return buildEmptyResult();
        }

        String lowerText = resumeText.toLowerCase();

        // --- Component 1: Keyword Score (40 pts) ---
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String keyword : CORE_KEYWORDS) {
            if (lowerText.contains(keyword.toLowerCase())) {
                matched.add(keyword);
            } else {
                missing.add(keyword);
            }
        }
        int keywordScore = (int) Math.round((matched.size() * 1.0 / CORE_KEYWORDS.size()) * 40);

        // --- Component 2: Section Completeness (25 pts) ---
        long sectionsFound = REQUIRED_SECTIONS.stream()
                .filter(s -> lowerText.contains(s))
                .count();
        int sectionScore = (int) Math.round((sectionsFound * 1.0 / REQUIRED_SECTIONS.size()) * 25);

        // --- Component 3: Technical Skills Score (20 pts) ---
        int skillsScore = calculateSkillsScore(lowerText);

        // --- Component 4: Formatting Score (15 pts) ---
        int formattingScore = calculateFormattingScore(resumeText);

        // --- Total ---
        int overallScore = Math.min(keywordScore + sectionScore + skillsScore + formattingScore, 100);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("overallScore",        overallScore);
        result.put("keywordScore",        keywordScore);
        result.put("sectionScore",        sectionScore);
        result.put("skillsScore",         skillsScore);
        result.put("formattingScore",     formattingScore);
        result.put("matchedKeywords",     matched);
        result.put("missingKeywords",     missing.stream().limit(20).collect(Collectors.toList()));
        result.put("scoreGrade",          getGrade(overallScore));
        result.put("scoreMessage",        getScoreMessage(overallScore));

        return result;
    }

    private int calculateSkillsScore(String text) {
        int score = 0;
        for (Map.Entry<String, Integer> entry : SKILL_WEIGHTS.entrySet()) {
            if (text.contains(entry.getKey())) {
                score += entry.getValue();
            }
        }
        return Math.min(score, 20);
    }

    private int calculateFormattingScore(String text) {
        int score = 0;
        int wordCount = text.split("\\s+").length;

        // Ideal resume length: 300–700 words
        if (wordCount >= 300 && wordCount <= 700)      score += 6;
        else if (wordCount >= 200 && wordCount < 300)  score += 3;
        else if (wordCount > 700 && wordCount <= 1000) score += 4;

        // Has bullet points or structured lists
        if (text.contains("•") || text.contains("·") ||
            text.contains("-") || text.contains("*"))  score += 3;

        // Has contact information
        if (text.contains("@"))                        score += 2;
        if (text.contains("github") ||
            text.contains("linkedin"))                 score += 2;

        // Has phone number pattern
        if (text.matches("(?s).*\\d{10}.*") ||
            text.matches("(?s).*\\+\\d.*"))            score += 2;

        return Math.min(score, 15);
    }

    private String getGrade(int score) {
        if (score >= 85) return "A";
        if (score >= 70) return "B";
        if (score >= 55) return "C";
        if (score >= 40) return "D";
        return "F";
    }

    private String getScoreMessage(int score) {
        if (score >= 85) return "Excellent — Very likely to pass ATS screening";
        if (score >= 70) return "Good — Likely to pass most ATS systems";
        if (score >= 55) return "Average — May pass some ATS systems";
        if (score >= 40) return "Below Average — Needs significant improvement";
        return "Poor — Major improvements required";
    }

    private Map<String, Object> buildEmptyResult() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("overallScore",    0);
        result.put("keywordScore",    0);
        result.put("sectionScore",    0);
        result.put("skillsScore",     0);
        result.put("formattingScore", 0);
        result.put("matchedKeywords", Collections.emptyList());
        result.put("missingKeywords", CORE_KEYWORDS);
        result.put("scoreGrade",      "F");
        result.put("scoreMessage",    "No resume content found");
        return result;
    }
}
