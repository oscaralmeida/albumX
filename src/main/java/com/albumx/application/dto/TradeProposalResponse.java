package com.albumx.application.dto;

import com.albumx.domain.model.TradeStatus;

import java.time.Instant;
import java.util.UUID;

public record TradeProposalResponse(
        UUID id,
        UUID requesterUserId,
        UUID targetUserId,
        int offeredStickerNumber,
        int requestedStickerNumber,
        TradeStatus status,
        Instant createdAt
) {
}
