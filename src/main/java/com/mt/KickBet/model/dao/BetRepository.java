package com.mt.KickBet.model.dao;

import com.mt.KickBet.model.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findAllByUserId(Long userId);

    List<Bet> findAllByMatchId(Long matchId);

    void deleteAllByMatchId(Long matchId);

    Optional <Bet> findByUserIdAndMatchId(Long userId, Long matchId);

    List<Bet> findAllByUserIdAndWinningBetIsTrue(Long userId);
}
