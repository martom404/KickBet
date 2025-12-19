package com.mt.KickBet.service;

import com.mt.KickBet.model.dao.MatchRepository;
import com.mt.KickBet.model.dto.api.ApiMatch;
import com.mt.KickBet.model.dto.api.ApiResponse;
import com.mt.KickBet.model.entity.Match;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class ApiService {

    private final MatchRepository matchRepository;
    private final RestTemplate restTemplate;

    public ApiService(MatchRepository matchRepository, RestTemplate restTemplate) {
        this.matchRepository = matchRepository;
        this.restTemplate = restTemplate;
    }

    @Value("${football.api.base-url}")
    private String baseUrl;

    @Value("${football.api.token}")
    private String apiToken;

    @Value("${football.api.competition-id}")
    private String competitionIds;

    @Transactional
    public int syncMatches(int maxMatches) {
        try {
            String[] leagues = competitionIds.split(",");
            int savedCount = 0;

            for (String competitionId : leagues) {
                if (savedCount >= maxMatches) break;

                try {
                    String url = String.format("%s/competitions/%s/matches", baseUrl, competitionId);

                    HttpHeaders headers = new HttpHeaders();
                    headers.set("X-Auth-Token", apiToken);
                    HttpEntity<Void> entity = new HttpEntity<>(headers);

                    ResponseEntity<ApiResponse> response = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            ApiResponse.class
                    );

                    ApiResponse apiResponse = response.getBody();

                    if (apiResponse == null || apiResponse.matches() == null) {
                        log.warn("Brak meczów w odpowiedzi API dla ligi: {}", competitionId);
                        continue;
                    }

                    for (ApiMatch apiMatch : apiResponse.matches()) {

                        if (savedCount >= maxMatches) break;

                        Optional<Match> existingMatch = matchRepository.findByExternalId(apiMatch.getId());
                        if (existingMatch.isPresent()) continue;

                        if ("FINISHED".equals(apiMatch.getStatus()) || "LIVE".equals(apiMatch.getStatus()) || "CANCELLED".equals(apiMatch.getStatus()))
                            continue;

                        LocalDateTime startTime = apiMatch.getStartTime();
                        if (startTime == null) continue;

                        if (startTime.isBefore(LocalDateTime.now())) continue;
                        
                        Match match = Match.builder()
                                .externalId(apiMatch.getId())
                                .homeTeam(apiMatch.getHomeTeamName())
                                .awayTeam(apiMatch.getAwayTeamName())
                                .startTime(startTime)
                                .finalResult(null)
                                .oddsHome(apiMatch.getHomeWinOdds())
                                .oddsDraw(apiMatch.getDrawOdds())
                                .oddsAway(apiMatch.getAwayWinOdds())
                                .build();

                        matchRepository.save(match);
                        savedCount++;

                        log.info("Zapisano mecz: {} vs {} ({})", match.getHomeTeam(), match.getAwayTeam(), competitionId);
                    }

                } catch (Exception e) {
                    log.error("Błąd podczas pobierania meczów z ligi {}: {}", competitionId, e.getMessage());
                }
            }
            log.info("Synchronizacja zakończona. Zapisano {} nowych meczów", savedCount);
            return savedCount;
        } catch (Exception e) {
            log.error("Błąd podczas synchronizacji meczów z API", e);
            throw new RuntimeException("Nie udało się pobrać meczów z API: " + e.getMessage(), e);
        }
    }
}

