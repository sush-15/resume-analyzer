package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.response.AtsScoreResponse;
import com.resumeanalyzer.dto.response.DashboardResponse;
import com.resumeanalyzer.dto.response.JobMatchResponse;
import com.resumeanalyzer.entity.AtsScore;
import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.entity.SkillMatch;
import com.resumeanalyzer.entity.User;
import com.resumeanalyzer.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final AtsScoreRepository atsScoreRepository;
    private final SkillMatchRepository skillMatchRepository;

    public DashboardService(UserRepository userRepository,
                            ResumeRepository resumeRepository,
                            AtsScoreRepository atsScoreRepository,
                            SkillMatchRepository skillMatchRepository) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.atsScoreRepository = atsScoreRepository;
        this.skillMatchRepository = skillMatchRepository;
    }

    public DashboardResponse getDashboard(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Resume> resumes = resumeRepository.findByUserAndIsActiveTrueOrderByUploadedAtDesc(user);
        List<AtsScore> scores = atsScoreRepository.findByUserOrderByAnalyzedAtDesc(user);
        List<SkillMatch> skillMatches = skillMatchRepository.findByUserOrderByMatchScoreDesc(user);

        int totalResumes = resumes.size();
        Double avgScore = atsScoreRepository.findAverageScoreByUser(user);
        int bestScore = scores.stream().mapToInt(AtsScore::getOverallScore).max().orElse(0);

        List<AtsScoreResponse> recentScores = scores.stream()
                .limit(5).map(this::mapAtsScore).collect(Collectors.toList());

        List<JobMatchResponse> topMatches = skillMatches.stream()
                .limit(5).map(this::mapSkillMatch).collect(Collectors.toList());

        return DashboardResponse.builder()
                .userName(user.getFullName())
                .totalResumes(totalResumes)
                .averageAtsScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0)
                .bestAtsScore(bestScore)
                .totalJobMatches(skillMatches.size())
                .recentScores(recentScores)
                .topJobMatches(topMatches)
                .topMissingSkills(getTopSkills(scores, true))
                .topMatchedSkills(getTopSkills(scores, false))
                .build();
    }

    private AtsScoreResponse mapAtsScore(AtsScore score) {
        AtsScoreResponse r = new AtsScoreResponse();
        r.setId(score.getId());
        r.setResumeId(score.getResume().getId());
        r.setFileName(score.getResume().getFileName());
        r.setOverallScore(score.getOverallScore());
        r.setKeywordScore(score.getKeywordScore());
        r.setFormattingScore(score.getFormattingScore());
        r.setSkillsScore(score.getSkillsScore());
        r.setAnalyzedAt(score.getAnalyzedAt());
        return r;
    }

    private JobMatchResponse mapSkillMatch(SkillMatch sm) {
        JobMatchResponse r = new JobMatchResponse();
        r.setJobId(sm.getJob().getId());
        r.setJobTitle(sm.getJob().getTitle());
        r.setCompany(sm.getJob().getCompany());
        r.setMatchScore(sm.getMatchScore());
        r.setMatchLevel(getMatchLevel(sm.getMatchScore()));
        return r;
    }

    private List<String> getTopSkills(List<AtsScore> scores, boolean missing) {
        Map<String, Integer> freq = new HashMap<>();
        for (AtsScore score : scores) {
            String json = missing ? score.getMissingKeywords() : score.getMatchedKeywords();
            if (json == null) continue;
            String[] parts = json.replace("[","").replace("]","").replace("\"","").split(",");
            for (String s : parts) {
                String skill = s.trim();
                if (!skill.isEmpty()) freq.merge(skill, 1, Integer::sum);
            }
        }
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private String getMatchLevel(int score) {
        if (score >= 80) return "Excellent Match";
        if (score >= 60) return "Good Match";
        if (score >= 40) return "Partial Match";
        return "Low Match";
    }
}
