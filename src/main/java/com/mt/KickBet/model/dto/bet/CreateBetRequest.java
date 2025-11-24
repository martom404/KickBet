package com.mt.KickBet.model.dto.bet;

import com.mt.KickBet.model.entity.Result;
import jakarta.validation.constraints.NotNull;

public record CreateBetRequest(
        @NotNull(message = "Wynik jest wymagany.")
        Result pick
) {
}