package com.mt.KickBet.service;

import com.mt.KickBet.exception.NoMatchException;
import com.mt.KickBet.exception.NoUserException;
import com.mt.KickBet.model.dao.BetRepository;
import com.mt.KickBet.model.dao.MatchRepository;
import com.mt.KickBet.model.dao.UserRepository;
import com.mt.KickBet.model.dto.bet.CreateBetRequest;
import com.mt.KickBet.model.entity.Bet;
import com.mt.KickBet.model.entity.Match;
import com.mt.KickBet.model.entity.Result;
import com.mt.KickBet.model.entity.User;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BetService {

    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public BetService(BetRepository betRepository, UserRepository userRepository, MatchRepository matchRepository) {
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional
    public void createBet(Long userId, Long matchId, CreateBetRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoUserException("Brak użytkownika w systemie."));

        Match match = matchRepository.findById(matchId).orElseThrow(() -> new NoMatchException("Brak meczu w systemie."));

        LocalDateTime now = LocalDateTime.now();
        if(!match.getStartTime().isAfter(now)) {
            throw new IllegalStateException("Nie możesz obstawić meczu, który już się rozpoczął.");
        }

        if(Boolean.TRUE.equals(match.getHidden())) {
            throw new IllegalStateException("Nie możesz obstawić meczu, który jest niedostępny.");
        }

        Optional<Bet> existingBet = betRepository.findByUserIdAndMatchId(userId, matchId);

        if(existingBet.isPresent()) {
            Bet bet = existingBet.get();
            bet.setPick(request.pick());
            betRepository.save(bet);
        } else {
            Bet bet = Bet.builder()
                     .user(user)
                     .match(match)
                     .pick(request.pick())
                     .build();
            betRepository.save(bet);
        }
    }

    @Transactional
    public List<Bet> getUserBets(Long userId) {
        return betRepository.findAllByUserId(userId);
    }

    @Transactional
    public void awardPointsForMatch(Long matchId, Result finalResult) {
       List<Bet> bets = betRepository.findAllByMatchId(matchId);
       final int POINTS_AWARDED = 10;

       for(Bet bet : bets) {
           if(bet.getPick().equals(finalResult)) {
               bet.setPointsAwarded(POINTS_AWARDED);
               betRepository.save(bet);
               User user = bet.getUser();
               user.setPoints(user.getPoints() + POINTS_AWARDED);
               userRepository.save(user);
           }
       }
    }
}
