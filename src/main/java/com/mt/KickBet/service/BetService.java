package com.mt.KickBet.service;

import com.mt.KickBet.model.dao.BetRepository;
import com.mt.KickBet.model.entity.Bet;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BetService {

    private final BetRepository betRepository;

    public BetService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @Transactional
    public List<Bet> getUserBets(Long userId) {
        return betRepository.findAllByUserId(userId);
    }
}
