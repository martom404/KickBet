package com.mt.KickBet.dto.match;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateMatchRequest(
        @NotBlank(message = "Nazwa drużyny gospodarzy jest wymagana")
        String homeTeam,

        @NotBlank(message = "Nazwa drużyny gości jest wymagana")
        String awayTeam,

        @NotNull(message = "Data rozpoczęcia meczu jest wymagana")
        @Future(message = "Mecz nie może być w przeszłości")
        LocalDateTime startTime
) {
}
