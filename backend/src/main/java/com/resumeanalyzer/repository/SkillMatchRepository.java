package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.entity.SkillMatch;
import com.resumeanalyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillMatchRepository extends JpaRepository<SkillMatch, Long> {

    List<SkillMatch> findByResumeOrderByMatchScoreDesc(Resume resume);

    List<SkillMatch> findByUserOrderByMatchScoreDesc(User user);
}
