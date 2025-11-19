package com.mt.KickBet.model.dto.match;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record UpdateMatchRequest(
        @NotBlank(message = "Nazwa drużyny gospodarzy jest wymagana")
        @Pattern(
                regexp = "^[\\p{L} .-]{2,80}$",
                message = "Dozwolone litery, spacje, kropka, myślnik (2-80 znaków)"
        )
        String homeTeam,

        @NotBlank(message = "Nazwa drużyny gości jest wymagana")
        @Pattern(
                regexp = "^[\\p{L} .-]{2,80}$",
                message = "Dozwolone litery, spacje, kropka, myślnik (2-80 znaków)"
        )
        String awayTeam,

        @NotNull(message = "Data rozpoczęcia meczu jest wymagana")
        @Future(message = "Mecz nie może być w przeszłości")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startTime
) {
}


