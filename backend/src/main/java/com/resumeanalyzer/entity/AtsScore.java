package com.resumeanalyzer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ats_scores")
public class AtsScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(name = "keyword_score")
    private Integer keywordScore;

    @Column(name = "formatting_score")
    private Integer formattingScore;

    @Column(name = "skills_score")
    private Integer skillsScore;

    @Column(name = "experience_score")
    private Integer experienceScore;

    @Column(name = "section_completeness")
    private Integer sectionCompleteness;

    @Column(name = "matched_keywords", columnDefinition = "TEXT")
    private String matchedKeywords;

    @Column(name = "missing_keywords", columnDefinition = "TEXT")
    private String missingKeywords;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    public AtsScore() {}

    @PrePersist
    protected void onCreate() { analyzedAt = LocalDateTime.now(); }

    // Getters
    public Long getId() { return id; }
    public Resume getResume() { return resume; }
    public User getUser() { return user; }
    public Integer getOverallScore() { return overallScore; }
    public Integer getKeywordScore() { return keywordScore; }
    public Integer getFormattingScore() { return formattingScore; }
    public Integer getSkillsScore() { return skillsScore; }
    public Integer getExperienceScore() { return experienceScore; }
    public Integer getSectionCompleteness() { return sectionCompleteness; }
    public String getMatchedKeywords() { return matchedKeywords; }
    public String getMissingKeywords() { return missingKeywords; }
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setResume(Resume resume) { this.resume = resume; }
    public void setUser(User user) { this.user = user; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    public void setKeywordScore(Integer keywordScore) { this.keywordScore = keywordScore; }
    public void setFormattingScore(Integer formattingScore) { this.formattingScore = formattingScore; }
    public void setSkillsScore(Integer skillsScore) { this.skillsScore = skillsScore; }
    public void setExperienceScore(Integer experienceScore) { this.experienceScore = experienceScore; }
    public void setSectionCompleteness(Integer sectionCompleteness) { this.sectionCompleteness = sectionCompleteness; }
    public void setMatchedKeywords(String matchedKeywords) { this.matchedKeywords = matchedKeywords; }
    public void setMissingKeywords(String missingKeywords) { this.missingKeywords = missingKeywords; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Resume resume;
        private User user;
        private Integer overallScore;
        private Integer keywordScore;
        private Integer formattingScore;
        private Integer skillsScore;
        private Integer experienceScore;
        private Integer sectionCompleteness;
        private String matchedKeywords;
        private String missingKeywords;

        public Builder resume(Resume resume) { this.resume = resume; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder overallScore(Integer v) { this.overallScore = v; return this; }
        public Builder keywordScore(Integer v) { this.keywordScore = v; return this; }
        public Builder formattingScore(Integer v) { this.formattingScore = v; return this; }
        public Builder skillsScore(Integer v) { this.skillsScore = v; return this; }
        public Builder experienceScore(Integer v) { this.experienceScore = v; return this; }
        public Builder sectionCompleteness(Integer v) { this.sectionCompleteness = v; return this; }
        public Builder matchedKeywords(String v) { this.matchedKeywords = v; return this; }
        public Builder missingKeywords(String v) { this.missingKeywords = v; return this; }

        public AtsScore build() {
            AtsScore a = new AtsScore();
            a.resume = this.resume;
            a.user = this.user;
            a.overallScore = this.overallScore;
            a.keywordScore = this.keywordScore;
            a.formattingScore = this.formattingScore;
            a.skillsScore = this.skillsScore;
            a.experienceScore = this.experienceScore;
            a.sectionCompleteness = this.sectionCompleteness;
            a.matchedKeywords = this.matchedKeywords;
            a.missingKeywords = this.missingKeywords;
            return a;
        }
    }
}
