package com.resumeanalyzer.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class AtsScoreResponse {
    private Long id;
    private Long resumeId;
    private String fileName;
    private Integer overallScore;
    private Integer keywordScore;
    private Integer formattingScore;
    private Integer skillsScore;
    private Integer experienceScore;
    private Integer sectionCompleteness;
    private List<String> matchedKeywords;
    private List<String> missingKeywords;
    private String scoreGrade;
    private String scoreMessage;
    private LocalDateTime analyzedAt;

    public AtsScoreResponse() {}

    public Long getId() { return id; }
    public Long getResumeId() { return resumeId; }
    public String getFileName() { return fileName; }
    public Integer getOverallScore() { return overallScore; }
    public Integer getKeywordScore() { return keywordScore; }
    public Integer getFormattingScore() { return formattingScore; }
    public Integer getSkillsScore() { return skillsScore; }
    public Integer getExperienceScore() { return experienceScore; }
    public Integer getSectionCompleteness() { return sectionCompleteness; }
    public List<String> getMatchedKeywords() { return matchedKeywords; }
    public List<String> getMissingKeywords() { return missingKeywords; }
    public String getScoreGrade() { return scoreGrade; }
    public String getScoreMessage() { return scoreMessage; }
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }

    public void setId(Long id) { this.id = id; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    public void setKeywordScore(Integer keywordScore) { this.keywordScore = keywordScore; }
    public void setFormattingScore(Integer formattingScore) { this.formattingScore = formattingScore; }
    public void setSkillsScore(Integer skillsScore) { this.skillsScore = skillsScore; }
    public void setExperienceScore(Integer experienceScore) { this.experienceScore = experienceScore; }
    public void setSectionCompleteness(Integer sectionCompleteness) { this.sectionCompleteness = sectionCompleteness; }
    public void setMatchedKeywords(List<String> matchedKeywords) { this.matchedKeywords = matchedKeywords; }
    public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }
    public void setScoreGrade(String scoreGrade) { this.scoreGrade = scoreGrade; }
    public void setScoreMessage(String scoreMessage) { this.scoreMessage = scoreMessage; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id, resumeId;
        private String fileName, scoreGrade, scoreMessage;
        private Integer overallScore, keywordScore, formattingScore, skillsScore, experienceScore, sectionCompleteness;
        private List<String> matchedKeywords, missingKeywords;
        private LocalDateTime analyzedAt;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder resumeId(Long v) { this.resumeId = v; return this; }
        public Builder fileName(String v) { this.fileName = v; return this; }
        public Builder overallScore(Integer v) { this.overallScore = v; return this; }
        public Builder keywordScore(Integer v) { this.keywordScore = v; return this; }
        public Builder formattingScore(Integer v) { this.formattingScore = v; return this; }
        public Builder skillsScore(Integer v) { this.skillsScore = v; return this; }
        public Builder experienceScore(Integer v) { this.experienceScore = v; return this; }
        public Builder sectionCompleteness(Integer v) { this.sectionCompleteness = v; return this; }
        public Builder matchedKeywords(List<String> v) { this.matchedKeywords = v; return this; }
        public Builder missingKeywords(List<String> v) { this.missingKeywords = v; return this; }
        public Builder scoreGrade(String v) { this.scoreGrade = v; return this; }
        public Builder scoreMessage(String v) { this.scoreMessage = v; return this; }
        public Builder analyzedAt(LocalDateTime v) { this.analyzedAt = v; return this; }

        public AtsScoreResponse build() {
            AtsScoreResponse r = new AtsScoreResponse();
            r.id = id; r.resumeId = resumeId; r.fileName = fileName;
            r.overallScore = overallScore; r.keywordScore = keywordScore;
            r.formattingScore = formattingScore; r.skillsScore = skillsScore;
            r.experienceScore = experienceScore; r.sectionCompleteness = sectionCompleteness;
            r.matchedKeywords = matchedKeywords; r.missingKeywords = missingKeywords;
            r.scoreGrade = scoreGrade; r.scoreMessage = scoreMessage; r.analyzedAt = analyzedAt;
            return r;
        }
    }
}
