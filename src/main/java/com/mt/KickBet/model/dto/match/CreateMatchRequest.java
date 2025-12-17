package com.mt.KickBet.model.dto.match;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record CreateMatchRequest(
        @NotBlank(message = "Nazwa drużyny gospodarzy jest wymagana")
        @Pattern(
                regexp = "^[\\p{L} .&-]{2,80}$",
                message = "Dozwolone litery, spacje, kropka, myślnik i & (2-80 znaków)"
        )
        String homeTeam,

        @NotBlank(message = "Nazwa drużyny gości jest wymagana")
        @Pattern(
                regexp = "^[\\p{L} .&-]{2,80}$",
                message = "Dozwolone litery, spacje, kropka, myślnik i & (2-80 znaków)"
        )
        String awayTeam,

        @NotNull(message = "Data rozpoczęcia meczu jest wymagana")
        @Future(message = "Mecz nie może być w przeszłości")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startTime,

        @NotNull(message="Kurs na gospodarzy jest wymagany")
        @DecimalMin(value="1.0", message = "Kurs musi być >= 1.0")
        Double oddsHome,

        @NotNull(message="Kurs na remis jest wymagany")
        @DecimalMin(value="1.0", message = "Kurs musi być >= 1.0")
        Double oddsDraw,

        @NotNull(message="Kurs na gości jest wymagany")
        @DecimalMin(value="1.0", message = "Kurs musi być >= 1.0")
        Double oddsAway
) {
}