package com.mt.KickBet.service;

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

    public Page<Match> getAllMatches(int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.ASC, "startTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return matchRepository.findAvailableMatches(now, pageable);
    }

    public Page<Match> getAllMatchesForAdmin(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "startTime");
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
                .build();
        matchRepository.save(match);
    }

    @Transactional
    public void updateMatch(Long id, UpdateMatchRequest request) {
        matchRepository.findById(id).ifPresent(match -> {
            match.setHomeTeam(request.homeTeam());
            match.setAwayTeam(request.awayTeam());
            match.setStartTime(request.startTime());
            matchRepository.save(match);
        });
    }

    @Transactional
    public void setFinalResult(Long id, Result result) {
        matchRepository.findById(id).ifPresent(match -> {
            match.setFinalResult(result);
            match.setHidden(true);
            matchRepository.save(match);
            betService.awardPointsForMatch(id, result);
        });
    }

    @Transactional
    public void deleteMatch(Long id) {
        List<Bet> bets = betRepository.findAllByMatchId(id);

        for (Bet bet : bets) {
            if(bet.getPointsAwarded() != 0) {
                User user = bet.getUser();
                user.setPoints(user.getPoints() - bet.getPointsAwarded());
                userRepository.save(user);
            }
        }
        betRepository.deleteAllByMatchId(id);
        matchRepository.deleteById(id);
    }

    @Transactional
    public void makeHidden(Long id) {
        matchRepository.findById(id).ifPresent(match -> {
            match.setHidden(!Boolean.TRUE.equals(match.getHidden()));
            matchRepository.save(match);
        });
    }
}
