package com.mt.KickBet.model.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiMatch {

    private Long id;
    private String utcDate;
    private String status;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Team {
        private String name;
    }

    private Team homeTeam;
    private Team awayTeam;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Odds {
        private Double homeWin;
        private Double draw;
        private Double awayWin;
    }

    private Odds odds;

    public LocalDateTime getStartTime() {
        if (utcDate == null || utcDate.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(utcDate.replace("Z", ""),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (Exception e) {
            return null;
        }
    }

    public String getHomeTeamName() {
        return homeTeam != null ? homeTeam.getName() : null;
    }

    public String getAwayTeamName() {
        return awayTeam != null ? awayTeam.getName() : null;
    }

    public Double getHomeWinOdds() {
        return (odds != null && odds.getHomeWin() != null) ? odds.getHomeWin() : 1.0;
    }

    public Double getDrawOdds() {
        return (odds != null && odds.getDraw() != null) ? odds.getDraw() : 1.0;
    }

    public Double getAwayWinOdds() {
        return (odds != null && odds.getAwayWin() != null) ? odds.getAwayWin() : 1.0;
    }
}