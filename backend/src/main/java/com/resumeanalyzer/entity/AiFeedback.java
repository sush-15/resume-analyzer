package com.resumeanalyzer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_feedback")
public class AiFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "feedback_text", columnDefinition = "LONGTEXT")
    private String feedbackText;

    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(name = "improvements", columnDefinition = "TEXT")
    private String improvements;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    public AiFeedback() {}

    @PrePersist
    protected void onCreate() { generatedAt = LocalDateTime.now(); }

    // Getters
    public Long getId() { return id; }
    public Resume getResume() { return resume; }
    public User getUser() { return user; }
    public String getFeedbackText() { return feedbackText; }
    public String getSuggestions() { return suggestions; }
    public String getMissingSkills() { return missingSkills; }
    public String getImprovements() { return improvements; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setResume(Resume resume) { this.resume = resume; }
    public void setUser(User user) { this.user = user; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }
    public void setMissingSkills(String missingSkills) { this.missingSkills = missingSkills; }
    public void setImprovements(String improvements) { this.improvements = improvements; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Resume resume;
        private User user;
        private String feedbackText;
        private String suggestions;
        private String missingSkills;
        private String improvements;

        public Builder resume(Resume resume) { this.resume = resume; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder feedbackText(String feedbackText) { this.feedbackText = feedbackText; return this; }
        public Builder suggestions(String suggestions) { this.suggestions = suggestions; return this; }
        public Builder missingSkills(String missingSkills) { this.missingSkills = missingSkills; return this; }
        public Builder improvements(String improvements) { this.improvements = improvements; return this; }

        public AiFeedback build() {
            AiFeedback f = new AiFeedback();
            f.resume = this.resume;
            f.user = this.user;
            f.feedbackText = this.feedbackText;
            f.suggestions = this.suggestions;
            f.missingSkills = this.missingSkills;
            f.improvements = this.improvements;
            return f;
        }
    }
}
