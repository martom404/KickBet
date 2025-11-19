package com.mt.KickBet.model.dto.football;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballApiResponse {

    @JsonProperty("matches")
    private List<FootballApiMatch> matches;
}