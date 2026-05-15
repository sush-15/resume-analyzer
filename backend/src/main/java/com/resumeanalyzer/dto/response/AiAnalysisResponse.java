package com.resumeanalyzer.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class AiAnalysisResponse {
    private Long id;
    private Long resumeId;
    private String feedbackText;
    private List<String> suggestions;
    private List<String> missingSkills;
    private List<String> improvements;
    private LocalDateTime generatedAt;

    public AiAnalysisResponse() {}

    public Long getId() { return id; }
    public Long getResumeId() { return resumeId; }
    public String getFeedbackText() { return feedbackText; }
    public List<String> getSuggestions() { return suggestions; }
    public List<String> getMissingSkills() { return missingSkills; }
    public List<String> getImprovements() { return improvements; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
    public void setImprovements(List<String> improvements) { this.improvements = improvements; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id, resumeId;
        private String feedbackText;
        private List<String> suggestions, missingSkills, improvements;
        private LocalDateTime generatedAt;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder resumeId(Long v) { this.resumeId = v; return this; }
        public Builder feedbackText(String v) { this.feedbackText = v; return this; }
        public Builder suggestions(List<String> v) { this.suggestions = v; return this; }
        public Builder missingSkills(List<String> v) { this.missingSkills = v; return this; }
        public Builder improvements(List<String> v) { this.improvements = v; return this; }
        public Builder generatedAt(LocalDateTime v) { this.generatedAt = v; return this; }

        public AiAnalysisResponse build() {
            AiAnalysisResponse r = new AiAnalysisResponse();
            r.id = id; r.resumeId = resumeId; r.feedbackText = feedbackText;
            r.suggestions = suggestions; r.missingSkills = missingSkills;
            r.improvements = improvements; r.generatedAt = generatedAt;
            return r;
        }
    }
}
