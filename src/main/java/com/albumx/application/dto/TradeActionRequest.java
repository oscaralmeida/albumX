package com.albumx.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TradeActionRequest(
        @NotNull UUID targetUserId
) {
}
