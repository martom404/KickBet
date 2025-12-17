package com.mt.KickBet.service;

import com.mt.KickBet.exception.NoMatchException;
import com.mt.KickBet.model.dao.BetRepository;
import com.mt.KickBet.model.dao.MatchRepository;
import com.mt.KickBet.model.dao.UserRepository;
import com.mt.KickBet.model.dto.match.CreateMatchRequest;
import com.mt.KickBet.model.dto.match.UpdateMatchRequest;
import com.mt.KickBet.model.entity.Bet;
import com.mt.KickBet.model.entity.Match;
import com.mt.KickBet.model.entity.Result;
import com.mt.KickBet.model.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final BetService betService;

    public MatchService(MatchRepository matchRepository, BetRepository betRepository, UserRepository userRepository ,BetService betService) {
        this.matchRepository = matchRepository;
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.betService = betService;
    }

    public Page<Match> getAllMatches(int page, int size, String sortBy, String sortDir) {
        LocalDateTime now = LocalDateTime.now();
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return matchRepository.findAvailableMatches(now, pageable);
    }

    public Page<Match> getAllMatchesForAdmin(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return matchRepository.findAll(pageable);
    }

    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }

    @Transactional
    public void createMatch(CreateMatchRequest request) {
        Match match = Match.builder()
                .homeTeam(request.homeTeam())
                .awayTeam(request.awayTeam())
                .startTime(request.startTime())
                .oddsHome(request.oddsHome())
                .oddsDraw(request.oddsDraw())
                .oddsAway(request.oddsAway())
                .build();
        matchRepository.save(match);
    }

    @Transactional
    public void updateMatch(Long id, UpdateMatchRequest request) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new NoMatchException("Brak meczu w systemie."));

        match.setHomeTeam(request.homeTeam());
        match.setAwayTeam(request.awayTeam());
        match.setStartTime(request.startTime());
        match.setOddsHome(request.oddsHome());
        match.setOddsDraw(request.oddsDraw());
        match.setOddsAway(request.oddsAway());
        matchRepository.save(match);
    }

    @Transactional
    public void setFinalResult(Long id, Result result) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new NoMatchException("Brak meczu w systemie."));

        LocalDateTime now = LocalDateTime.now();
        if(match.getStartTime().isAfter(now)) {
            throw new IllegalStateException("Nie można ustawić wyniku dla meczu, który jeszcze się nie rozpoczął.");
        }

        match.setFinalResult(result);
        match.setHidden(true);
        matchRepository.save(match);
        betService.awardPointsForMatch(id, result);
    }

    @Transactional
    public void deleteMatch(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new NoMatchException("Brak meczu w systemie."));

        List<Bet> bets = betRepository.findAllByMatchId(id);

        for (Bet bet : bets) {
            if(bet.getPointsAwarded() != null && bet.getPointsAwarded() > 0.0) {
                User user = bet.getUser();
                user.setPoints(user.getPoints() - bet.getPointsAwarded());
                userRepository.save(user);
            }
        }
        betRepository.deleteAllByMatchId(id);
        matchRepository.delete(match);
    }

    @Transactional
    public void makeHidden(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new NoMatchException("Brak meczu w systemie."));

        match.setHidden(!match.isHidden());
        matchRepository.save(match);
    }
}
