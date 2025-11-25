package com.mt.KickBet.model.dao;

import com.mt.KickBet.model.entity.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByExternalId(Long externalId);
    
    @Query("SELECT m FROM Match m WHERE (m.hidden = false) AND m.finalResult IS NULL AND m.startTime > :startTime")
    Page<Match> findAvailableMatches(@Param("startTime") LocalDateTime startTime, Pageable pageable);
}
