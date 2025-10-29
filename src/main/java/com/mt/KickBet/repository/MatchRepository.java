package com.mt.KickBet.repository;

import com.mt.KickBet.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByStartTimeAfterOrderByStartTimeAsc(LocalDateTime from);
}
