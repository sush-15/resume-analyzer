package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.response.AiAnalysisResponse;
import com.resumeanalyzer.dto.response.AtsScoreResponse;
import com.resumeanalyzer.entity.AtsScore;
import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.service.AiAnalysisService;
import com.resumeanalyzer.service.AtsScoreService;
import com.resumeanalyzer.service.JobMatchService;
import com.resumeanalyzer.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeService resumeService;
    private final AtsScoreService atsScoreService;
    private final AiAnalysisService aiAnalysisService;
    private final JobMatchService jobMatchService;

    public ResumeController(ResumeService resumeService, AtsScoreService atsScoreService,
                            AiAnalysisService aiAnalysisService, JobMatchService jobMatchService) {
        this.resumeService = resumeService;
        this.atsScoreService = atsScoreService;
        this.aiAnalysisService = aiAnalysisService;
        this.jobMatchService = jobMatchService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        Resume resume = resumeService.uploadResume(file, authentication.getName());
        AtsScore atsScore = atsScoreService.calculateAndSave(resume);
        aiAnalysisService.generateAndSaveFeedback(resume);
        jobMatchService.matchResumeToJobs(resume);

        return ResponseEntity.ok(Map.of(
            "message",    "Resume uploaded and analyzed successfully!",
            "resumeId",   resume.getId(),
            "fileName",   resume.getFileName(),
            "atsScore",   atsScore.getOverallScore(),
            "scoreGrade", getGrade(atsScore.getOverallScore())
        ));
    }

    @GetMapping("/my-resumes")
    public ResponseEntity<List<Resume>> getMyResumes(Authentication authentication) {
        return ResponseEntity.ok(resumeService.getResumesByUser(authentication.getName()));
    }

    @GetMapping("/{resumeId}/ats-score")
    public ResponseEntity<AtsScoreResponse> getAtsScore(@PathVariable Long resumeId) {
        return ResponseEntity.ok(atsScoreService.getByResumeId(resumeId));
    }

    @GetMapping("/{resumeId}/ai-feedback")
    public ResponseEntity<AiAnalysisResponse> getAiFeedback(@PathVariable Long resumeId) {
        return ResponseEntity.ok(aiAnalysisService.getFeedbackByResumeId(resumeId));
    }

    @GetMapping("/{resumeId}/job-matches")
    public ResponseEntity<?> getJobMatches(@PathVariable Long resumeId) {
        return ResponseEntity.ok(jobMatchService.getMatchesByResumeId(resumeId));
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Map<String, String>> deleteResume(
            @PathVariable Long resumeId, Authentication authentication) {
        resumeService.deleteResume(resumeId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Resume deleted successfully"));
    }

    /**
     * Re-run ATS scoring, AI feedback, and job matching for an existing resume.
     * Useful when the user wants fresh analysis (e.g., after job list changes).
     */
    @PostMapping("/{resumeId}/reanalyze")
    public ResponseEntity<Map<String, Object>> reanalyzeResume(
            @PathVariable Long resumeId, Authentication authentication) {
        Resume resume = resumeService.getResumeById(resumeId, authentication.getName());
        AtsScore atsScore = atsScoreService.calculateAndSave(resume);
        aiAnalysisService.generateAndSaveFeedback(resume);
        jobMatchService.matchResumeToJobs(resume);

        return ResponseEntity.ok(Map.of(
            "message",    "Resume re-analyzed successfully!",
            "resumeId",   resume.getId(),
            "atsScore",   atsScore.getOverallScore(),
            "scoreGrade", getGrade(atsScore.getOverallScore())
        ));
    }

    private String getGrade(int score) {
        if (score >= 85) return "A";
        if (score >= 70) return "B";
        if (score >= 55) return "C";
        if (score >= 40) return "D";
        return "F";
    }
}
