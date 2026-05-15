package com.resumeanalyzer.dto.response;

import java.util.List;

public class DashboardResponse {
    private String userName;
    private Integer totalResumes;
    private Double averageAtsScore;
    private Integer bestAtsScore;
    private Integer totalJobMatches;
    private List<AtsScoreResponse> recentScores;
    private List<JobMatchResponse> topJobMatches;
    private List<String> topMissingSkills;
    private List<String> topMatchedSkills;

    public DashboardResponse() {}

    public String getUserName() { return userName; }
    public Integer getTotalResumes() { return totalResumes; }
    public Double getAverageAtsScore() { return averageAtsScore; }
    public Integer getBestAtsScore() { return bestAtsScore; }
    public Integer getTotalJobMatches() { return totalJobMatches; }
    public List<AtsScoreResponse> getRecentScores() { return recentScores; }
    public List<JobMatchResponse> getTopJobMatches() { return topJobMatches; }
    public List<String> getTopMissingSkills() { return topMissingSkills; }
    public List<String> getTopMatchedSkills() { return topMatchedSkills; }

    public void setUserName(String userName) { this.userName = userName; }
    public void setTotalResumes(Integer totalResumes) { this.totalResumes = totalResumes; }
    public void setAverageAtsScore(Double averageAtsScore) { this.averageAtsScore = averageAtsScore; }
    public void setBestAtsScore(Integer bestAtsScore) { this.bestAtsScore = bestAtsScore; }
    public void setTotalJobMatches(Integer totalJobMatches) { this.totalJobMatches = totalJobMatches; }
    public void setRecentScores(List<AtsScoreResponse> recentScores) { this.recentScores = recentScores; }
    public void setTopJobMatches(List<JobMatchResponse> topJobMatches) { this.topJobMatches = topJobMatches; }
    public void setTopMissingSkills(List<String> topMissingSkills) { this.topMissingSkills = topMissingSkills; }
    public void setTopMatchedSkills(List<String> topMatchedSkills) { this.topMatchedSkills = topMatchedSkills; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String userName;
        private Integer totalResumes, bestAtsScore, totalJobMatches;
        private Double averageAtsScore;
        private List<AtsScoreResponse> recentScores;
        private List<JobMatchResponse> topJobMatches;
        private List<String> topMissingSkills, topMatchedSkills;

        public Builder userName(String v) { this.userName = v; return this; }
        public Builder totalResumes(Integer v) { this.totalResumes = v; return this; }
        public Builder averageAtsScore(Double v) { this.averageAtsScore = v; return this; }
        public Builder bestAtsScore(Integer v) { this.bestAtsScore = v; return this; }
        public Builder totalJobMatches(Integer v) { this.totalJobMatches = v; return this; }
        public Builder recentScores(List<AtsScoreResponse> v) { this.recentScores = v; return this; }
        public Builder topJobMatches(List<JobMatchResponse> v) { this.topJobMatches = v; return this; }
        public Builder topMissingSkills(List<String> v) { this.topMissingSkills = v; return this; }
        public Builder topMatchedSkills(List<String> v) { this.topMatchedSkills = v; return this; }

        public DashboardResponse build() {
            DashboardResponse r = new DashboardResponse();
            r.userName = userName; r.totalResumes = totalResumes;
            r.averageAtsScore = averageAtsScore; r.bestAtsScore = bestAtsScore;
            r.totalJobMatches = totalJobMatches; r.recentScores = recentScores;
            r.topJobMatches = topJobMatches; r.topMissingSkills = topMissingSkills;
            r.topMatchedSkills = topMatchedSkills;
            return r;
        }
    }
}
