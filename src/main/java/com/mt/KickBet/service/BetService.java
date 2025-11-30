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

    private Double getOdds(Match match, Result pick) {
        return switch (pick) {
            case HOME -> match.getOddsHome();
            case DRAW -> match.getOddsDraw();
            case AWAY -> match.getOddsAway();
        };
    }

    @Transactional
    public List<Bet> getUserBets(Long userId) {
        return betRepository.findAllByUserId(userId);
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

        if(match.getFinalResult() != null) {
            throw new IllegalStateException("Nie możesz obstawić meczu, który już się zakończył.");
        }

        Optional<Bet> existingBet = betRepository.findByUserIdAndMatchId(userId, matchId);

        Bet bet;
        if(existingBet.isPresent()) {
            bet = existingBet.get();
            bet.setPick(request.pick());
            bet.setOddsAtBetTime(getOdds(match, request.pick()));
        } else {
            bet = Bet.builder()
                    .user(user)
                    .match(match)
                    .pick(request.pick())
                    .oddsAtBetTime(getOdds(match, request.pick()))
                    .build();
        }
        betRepository.save(bet);
    }

    @Transactional
    public void awardPointsForMatch(Long matchId, Result finalResult) {
       List<Bet> bets = betRepository.findAllByMatchId(matchId);

       for(Bet bet : bets) {
           if(bet.getPick().equals(finalResult)) {
               double pointsAwarded = (bet.getOddsAtBetTime() * 1.0);

               bet.setPointsAwarded(pointsAwarded);
               betRepository.save(bet);

               User user = bet.getUser();
               user.setPoints(user.getPoints() + pointsAwarded);
               userRepository.save(user);
           }
       }
    }
}
