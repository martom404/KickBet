package com.mt.KickBet.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterForm(
        @NotBlank(message = "Nazwa użytkownika jest wymagana do utworzenia konta")
        @Pattern(
                regexp = "^[\\p{L}0-9_.-]{4,40}$",
                message = "Nazwa użytkownika musi mieć od 4 do 40 znaków i może zawierać litery, cyfry oraz . _ -"
        )
        String username,

        @NotBlank(message = "Email jest wymagany do utworzenia konta")
        @Email(message = "Twój email musi mieć prawidłowy format!")
        String email,

        @NotBlank(message = "Musisz uzupełnić pole z hasłem!")
        @Size(min = 8, max = 64, message = "Hasło musi mieć od 8 do 64 znaków.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "Hasło musi zawierać przynajmniej jedną literę i jedną cyfrę."
        )
        String password,

        @NotBlank(message = "Musisz potwierdzić hasło!")
        String confirmPassword
) {
}