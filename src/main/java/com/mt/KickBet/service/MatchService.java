package com.mt.KickBet.service;

import com.mt.KickBet.model.dao.MatchRepository;
import com.mt.KickBet.model.dto.match.CreateMatchRequest;
import com.mt.KickBet.model.dto.match.UpdateMatchRequest;
import com.mt.KickBet.model.entity.Match;
import com.mt.KickBet.model.entity.Result;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getAllMatches() {
        LocalDateTime now = LocalDateTime.now();
        return matchRepository.findAll().stream()
                .filter(match -> !Boolean.TRUE.equals(match.getHidden()))
                .filter(match -> match.getFinalResult() == null)
                .filter(match -> match.getStartTime().isAfter(now))
                .collect(Collectors.toList());
    }

    public Page<Match> getAllMatchesForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
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
            matchRepository.save(match);
        });
    }

    @Transactional
    public void deleteMatch(Long id) {
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
