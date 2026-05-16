package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.Job;
import com.resumeanalyzer.service.JobMatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    private final JobMatchService jobMatchService;

    public JobController(JobMatchService jobMatchService) {
        this.jobMatchService = jobMatchService;
    }

    /** Public — no auth required */
    @GetMapping("/public/all")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobMatchService.getAllJobs());
    }

    /** Authenticated — match a resume against all jobs */
    @GetMapping("/match/{resumeId}")
    public ResponseEntity<?> matchJobs(@PathVariable Long resumeId) {
        return ResponseEntity.ok(jobMatchService.getMatchesByResumeId(resumeId));
    }

    /** Admin only — create a new job */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        return ResponseEntity.ok(jobMatchService.createJob(job));
    }

    /** Admin only — update an existing job */
    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Job> updateJob(@PathVariable Long jobId, @RequestBody Job job) {
        return ResponseEntity.ok(jobMatchService.updateJob(jobId, job));
    }

    /** Admin only — deactivate (soft-delete) a job */
    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteJob(@PathVariable Long jobId) {
        jobMatchService.deleteJob(jobId);
        return ResponseEntity.ok(Map.of("message", "Job deactivated successfully"));
    }
}
