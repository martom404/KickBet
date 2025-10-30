package com.mt.KickBet.repository;

import com.mt.KickBet.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findAllByUserId(Long userId);
    List<Bet> findAllByMatchId(Long matchId);
}
