package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.Job;
import com.resumeanalyzer.service.JobMatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    private final JobMatchService jobMatchService;

    public JobController(JobMatchService jobMatchService) {
        this.jobMatchService = jobMatchService;
    }

    @GetMapping("/public/all")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobMatchService.getAllJobs());
    }

    @GetMapping("/match/{resumeId}")
    public ResponseEntity<?> matchJobs(@PathVariable Long resumeId) {
        return ResponseEntity.ok(jobMatchService.getMatchesByResumeId(resumeId));
    }
}
