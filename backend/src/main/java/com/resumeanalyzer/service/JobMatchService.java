package com.resumeanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.dto.response.JobMatchResponse;
import com.resumeanalyzer.entity.Job;
import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.entity.SkillMatch;
import com.resumeanalyzer.repository.JobRepository;
import com.resumeanalyzer.repository.SkillMatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class JobMatchService {

    private static final Logger log = Logger.getLogger(JobMatchService.class.getName());

    private final JobRepository jobRepository;
    private final SkillMatchRepository skillMatchRepository;
    private final ObjectMapper objectMapper;

    public JobMatchService(JobRepository jobRepository,
                           SkillMatchRepository skillMatchRepository,
                           ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.skillMatchRepository = skillMatchRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<JobMatchResponse> matchResumeToJobs(Resume resume) {
        List<Job> allJobs = jobRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        String resumeText = resume.getExtractedText().toLowerCase();
        List<JobMatchResponse> matches = new ArrayList<>();

        for (Job job : allJobs) {
            JobMatchResponse match = calculateJobMatch(resume, job, resumeText);
            matches.add(match);
            saveSkillMatch(resume, job, match);
        }

        matches.sort((a, b) -> b.getMatchScore() - a.getMatchScore());
        log.info("Matched resume " + resume.getId() + " against " + allJobs.size() + " jobs");
        return matches;
    }

    public List<JobMatchResponse> getMatchesByResumeId(Long resumeId) {
        Resume resume = new Resume();
        resume.setId(resumeId);
        return skillMatchRepository.findByResumeOrderByMatchScoreDesc(resume)
                .stream()
                .map(this::mapSkillMatchToResponse)
                .collect(Collectors.toList());
    }

    public List<Job> getAllJobs() {
        return jobRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional
    public Job createJob(Job job) {
        job.setIsActive(true);
        Job saved = jobRepository.save(job);
        log.info("Created new job: " + saved.getTitle() + " (ID: " + saved.getId() + ")");
        return saved;
    }

    @Transactional
    public Job updateJob(Long jobId, Job updated) {
        Job existing = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
        existing.setTitle(updated.getTitle());
        existing.setCompany(updated.getCompany());
        existing.setDescription(updated.getDescription());
        existing.setRequiredSkills(updated.getRequiredSkills());
        existing.setExperienceLevel(updated.getExperienceLevel());
        existing.setJobType(updated.getJobType());
        existing.setLocation(updated.getLocation());
        Job saved = jobRepository.save(existing);
        log.info("Updated job ID: " + jobId);
        return saved;
    }

    @Transactional
    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
        job.setIsActive(false);
        jobRepository.save(job);
        log.info("Deactivated job ID: " + jobId);
    }

    private JobMatchResponse calculateJobMatch(Resume resume, Job job, String resumeText) {
        List<String> requiredSkills = parseSkillsJson(job.getRequiredSkills());
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : requiredSkills) {
            if (resumeText.contains(skill.toLowerCase())) matched.add(skill);
            else missing.add(skill);
        }

        int matchScore = requiredSkills.isEmpty() ? 0
                : (int) Math.round((matched.size() * 1.0 / requiredSkills.size()) * 100);

        return JobMatchResponse.builder()
                .jobId(job.getId())
                .jobTitle(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .jobType(job.getJobType() != null ? job.getJobType().name() : "N/A")
                .experienceLevel(job.getExperienceLevel().name())
                .matchScore(matchScore)
                .matchedSkills(matched)
                .missingSkills(missing)
                .matchLevel(getMatchLevel(matchScore))
                .build();
    }

    private void saveSkillMatch(Resume resume, Job job, JobMatchResponse match) {
        SkillMatch sm = SkillMatch.builder()
                .user(resume.getUser())
                .resume(resume)
                .job(job)
                .matchScore(match.getMatchScore())
                .matchedSkills(toJson(match.getMatchedSkills()))
                .missingSkills(toJson(match.getMissingSkills()))
                .build();
        skillMatchRepository.save(sm);
    }

    private JobMatchResponse mapSkillMatchToResponse(SkillMatch sm) {
        return JobMatchResponse.builder()
                .jobId(sm.getJob().getId())
                .jobTitle(sm.getJob().getTitle())
                .company(sm.getJob().getCompany())
                .location(sm.getJob().getLocation())
                .jobType(sm.getJob().getJobType() != null ? sm.getJob().getJobType().name() : "N/A")
                .experienceLevel(sm.getJob().getExperienceLevel().name())
                .matchScore(sm.getMatchScore())
                .matchedSkills(fromJson(sm.getMatchedSkills()))
                .missingSkills(fromJson(sm.getMissingSkills()))
                .matchLevel(getMatchLevel(sm.getMatchScore()))
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<String> parseSkillsJson(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) { return List.of(); }
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

    private String getMatchLevel(int score) {
        if (score >= 80) return "Excellent Match";
        if (score >= 60) return "Good Match";
        if (score >= 40) return "Partial Match";
        return "Low Match";
    }
}
