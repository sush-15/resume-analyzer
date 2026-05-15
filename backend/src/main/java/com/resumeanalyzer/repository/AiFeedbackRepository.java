package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.AiFeedback;
import com.resumeanalyzer.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {

    Optional<AiFeedback> findTopByResumeOrderByGeneratedAtDesc(Resume resume);
}
