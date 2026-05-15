package com.resumeanalyzer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_matches")
public class SkillMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "match_score", nullable = false)
    private Integer matchScore;

    @Column(name = "matched_skills", columnDefinition = "TEXT")
    private String matchedSkills;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;

    public SkillMatch() {}

    @PrePersist
    protected void onCreate() { matchedAt = LocalDateTime.now(); }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Resume getResume() { return resume; }
    public Job getJob() { return job; }
    public Integer getMatchScore() { return matchScore; }
    public String getMatchedSkills() { return matchedSkills; }
    public String getMissingSkills() { return missingSkills; }
    public LocalDateTime getMatchedAt() { return matchedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setResume(Resume resume) { this.resume = resume; }
    public void setJob(Job job) { this.job = job; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
    public void setMatchedSkills(String matchedSkills) { this.matchedSkills = matchedSkills; }
    public void setMissingSkills(String missingSkills) { this.missingSkills = missingSkills; }
    public void setMatchedAt(LocalDateTime matchedAt) { this.matchedAt = matchedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private Resume resume;
        private Job job;
        private Integer matchScore;
        private String matchedSkills;
        private String missingSkills;

        public Builder user(User user) { this.user = user; return this; }
        public Builder resume(Resume resume) { this.resume = resume; return this; }
        public Builder job(Job job) { this.job = job; return this; }
        public Builder matchScore(Integer matchScore) { this.matchScore = matchScore; return this; }
        public Builder matchedSkills(String matchedSkills) { this.matchedSkills = matchedSkills; return this; }
        public Builder missingSkills(String missingSkills) { this.missingSkills = missingSkills; return this; }

        public SkillMatch build() {
            SkillMatch s = new SkillMatch();
            s.user = this.user;
            s.resume = this.resume;
            s.job = this.job;
            s.matchScore = this.matchScore;
            s.matchedSkills = this.matchedSkills;
            s.missingSkills = this.missingSkills;
            return s;
        }
    }
}
