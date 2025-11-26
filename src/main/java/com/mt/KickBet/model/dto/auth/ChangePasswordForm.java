package com.mt.KickBet.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordForm (
  @NotBlank(message="Musisz wpisać aktualne hasło!")
  String currentPassword,

  @NotBlank(message="Musisz uzupełnic pole z nowym hasłem!")
  @Size(min=8, max=64, message="Hasło musi mieć od 8 do 64 znaków!")
  @Pattern(
          regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
          message = "Hasło musi zawierać przynajmniej jedną literę i jedną cyfrę."
  )
  String newPassword,

  @NotBlank(message="Musisz potwierdzić nowe hasło!")
  String confirmPassword
) { }
