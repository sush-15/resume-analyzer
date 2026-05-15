package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByIsActiveTrueOrderByCreatedAtDesc();

    List<Job> findByExperienceLevelAndIsActiveTrue(Job.ExperienceLevel level);

    List<Job> findByJobTypeAndIsActiveTrue(Job.JobType jobType);
}
