package com.mt.KickBet.service;

import com.mt.KickBet.dto.match.CreateMatchRequest;
import com.mt.KickBet.dto.match.UpdateMatchRequest;
import com.mt.KickBet.model.Match;
import com.mt.KickBet.model.Result;
import com.mt.KickBet.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Match createMatch(CreateMatchRequest request) {
        Match match = Match.builder()
                .homeTeam(request.homeTeam())
                .awayTeam(request.awayTeam())
                .startTime(request.startTime())
                .build();
        return matchRepository.save(match);
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll().stream()
                .sorted(Comparator.comparing(Match::getStartTime))
                .toList();
    }

    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }

    public Optional<Match> setFinalResult(Long matchId, Result result) {
        return matchRepository.findById(matchId).map(match -> {
            match.setFinalResult(result);
            return matchRepository.save(match);
        });
    }

    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

    public Optional<Match> updateMatch(Long matchId, UpdateMatchRequest request) {
        return matchRepository.findById(matchId).map(match -> {
            match.setHomeTeam(request.homeTeam());
            match.setAwayTeam(request.awayTeam());
            match.setStartTime(request.startTime());
            return matchRepository.save(match);
        });
    }
}
