package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.AtsScore;
import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtsScoreRepository extends JpaRepository<AtsScore, Long> {

    Optional<AtsScore> findTopByResumeOrderByAnalyzedAtDesc(Resume resume);

    List<AtsScore> findByUserOrderByAnalyzedAtDesc(User user);

    @Query("SELECT AVG(a.overallScore) FROM AtsScore a WHERE a.user = :user")
    Double findAverageScoreByUser(User user);
}
