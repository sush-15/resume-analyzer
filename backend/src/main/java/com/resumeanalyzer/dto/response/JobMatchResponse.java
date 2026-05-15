package com.resumeanalyzer.dto.response;

import java.util.List;

public class JobMatchResponse {
    private Long jobId;
    private String jobTitle;
    private String company;
    private String location;
    private String jobType;
    private String experienceLevel;
    private Integer matchScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String matchLevel;

    public JobMatchResponse() {}

    public Long getJobId() { return jobId; }
    public String getJobTitle() { return jobTitle; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getJobType() { return jobType; }
    public String getExperienceLevel() { return experienceLevel; }
    public Integer getMatchScore() { return matchScore; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
    public String getMatchLevel() { return matchLevel; }

    public void setJobId(Long jobId) { this.jobId = jobId; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setCompany(String company) { this.company = company; }
    public void setLocation(String location) { this.location = location; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
    public void setMatchLevel(String matchLevel) { this.matchLevel = matchLevel; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long jobId;
        private String jobTitle, company, location, jobType, experienceLevel, matchLevel;
        private Integer matchScore;
        private List<String> matchedSkills, missingSkills;

        public Builder jobId(Long v) { this.jobId = v; return this; }
        public Builder jobTitle(String v) { this.jobTitle = v; return this; }
        public Builder company(String v) { this.company = v; return this; }
        public Builder location(String v) { this.location = v; return this; }
        public Builder jobType(String v) { this.jobType = v; return this; }
        public Builder experienceLevel(String v) { this.experienceLevel = v; return this; }
        public Builder matchScore(Integer v) { this.matchScore = v; return this; }
        public Builder matchedSkills(List<String> v) { this.matchedSkills = v; return this; }
        public Builder missingSkills(List<String> v) { this.missingSkills = v; return this; }
        public Builder matchLevel(String v) { this.matchLevel = v; return this; }

        public JobMatchResponse build() {
            JobMatchResponse r = new JobMatchResponse();
            r.jobId = jobId; r.jobTitle = jobTitle; r.company = company;
            r.location = location; r.jobType = jobType; r.experienceLevel = experienceLevel;
            r.matchScore = matchScore; r.matchedSkills = matchedSkills;
            r.missingSkills = missingSkills; r.matchLevel = matchLevel;
            return r;
        }
    }
}
