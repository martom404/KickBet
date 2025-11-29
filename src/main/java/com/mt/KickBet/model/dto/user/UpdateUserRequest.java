package com.mt.KickBet.model.dto.user;

import jakarta.validation.constraints.*;

public record UpdateUserRequest(
        @NotBlank(message = "Nazwa użytkownika nie może być pusta.")
        @Pattern(
                regexp = "^[\\p{L}0-9_.-]{4,40}$",
                message = "Nazwa użytkownika musi mieć od 4 do 40 znaków i może zawierać litery, cyfry oraz . _ -"
        )
        String username,

        @NotBlank(message = "Email jest wymagany")
        @Email(message = "Nieprawidłowy format adresu email")
        String email,

        @NotNull
        Double points
) {
}
