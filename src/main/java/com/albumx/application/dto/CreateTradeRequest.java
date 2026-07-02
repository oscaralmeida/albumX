package com.albumx.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTradeRequest(
        @NotNull UUID requesterUserId,
        @NotNull UUID targetUserId,
        @NotNull @Min(1) Integer offeredStickerNumber,
        @NotNull @Min(1) Integer requestedStickerNumber
) {
}
