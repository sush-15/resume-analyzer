package com.resumeanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.dto.response.AtsScoreResponse;
import com.resumeanalyzer.entity.AtsScore;
import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.repository.AtsScoreRepository;
import com.resumeanalyzer.util.AtsScoreCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class AtsScoreService {

    private static final Logger log = Logger.getLogger(AtsScoreService.class.getName());

    private final AtsScoreRepository atsScoreRepository;
    private final AtsScoreCalculator atsScoreCalculator;
    private final ObjectMapper objectMapper;

    public AtsScoreService(AtsScoreRepository atsScoreRepository,
                           AtsScoreCalculator atsScoreCalculator,
                           ObjectMapper objectMapper) {
        this.atsScoreRepository = atsScoreRepository;
        this.atsScoreCalculator = atsScoreCalculator;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AtsScore calculateAndSave(Resume resume) {
        log.info("Calculating ATS score for resume ID: " + resume.getId());
        Map<String, Object> scoreData = atsScoreCalculator.calculateScore(resume.getExtractedText());

        AtsScore atsScore = AtsScore.builder()
                .resume(resume)
                .user(resume.getUser())
                .overallScore((Integer) scoreData.get("overallScore"))
                .keywordScore((Integer) scoreData.get("keywordScore"))
                .formattingScore((Integer) scoreData.get("formattingScore"))
                .skillsScore((Integer) scoreData.get("skillsScore"))
                .experienceScore((Integer) scoreData.get("sectionScore"))
                .sectionCompleteness((Integer) scoreData.get("sectionScore"))
                .matchedKeywords(toJson(scoreData.get("matchedKeywords")))
                .missingKeywords(toJson(scoreData.get("missingKeywords")))
                .build();

        AtsScore saved = atsScoreRepository.save(atsScore);
        log.info("ATS Score saved: " + saved.getOverallScore() + "/100 for resume ID: " + resume.getId());
        return saved;
    }

    public AtsScoreResponse getByResumeId(Long resumeId) {
        Resume resume = new Resume();
        resume.setId(resumeId);
        AtsScore score = atsScoreRepository.findTopByResumeOrderByAnalyzedAtDesc(resume)
                .orElseThrow(() -> new RuntimeException("ATS score not found for resume: " + resumeId));
        return mapToResponse(score);
    }

    private AtsScoreResponse mapToResponse(AtsScore score) {
        return AtsScoreResponse.builder()
                .id(score.getId())
                .resumeId(score.getResume().getId())
                .fileName(score.getResume().getFileName())
                .overallScore(score.getOverallScore())
                .keywordScore(score.getKeywordScore())
                .formattingScore(score.getFormattingScore())
                .skillsScore(score.getSkillsScore())
                .experienceScore(score.getExperienceScore())
                .sectionCompleteness(score.getSectionCompleteness())
                .matchedKeywords(fromJson(score.getMatchedKeywords()))
                .missingKeywords(fromJson(score.getMissingKeywords()))
                .scoreGrade(getGrade(score.getOverallScore()))
                .scoreMessage(getScoreMessage(score.getOverallScore()))
                .analyzedAt(score.getAnalyzedAt())
                .build();
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (JsonProcessingException e) { return "[]"; }
    }

    @SuppressWarnings("unchecked")
    private List<String> fromJson(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) { return List.of(); }
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
}
