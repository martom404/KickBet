package com.mt.KickBet.model.dto.football;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballApiMatch {

    private Long id;

    @JsonProperty("homeTeam")
    private Team homeTeam;

    @JsonProperty("awayTeam")
    private Team awayTeam;

    @JsonProperty("utcDate")
    private String utcDate;

    private String status;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Team {
        private String name;
    }

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
}