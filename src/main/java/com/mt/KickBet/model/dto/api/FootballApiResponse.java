package com.mt.KickBet.model.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FootballApiResponse(
        @JsonProperty("matches")
        List<FootballApiMatch> matches
) {}
