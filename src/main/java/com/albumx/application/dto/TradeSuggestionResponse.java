package com.albumx.application.dto;

import java.util.UUID;

public record TradeSuggestionResponse(
        UUID requesterUserId,
        UUID partnerUserId,
        int offeredStickerNumber,
        int requestedStickerNumber,
        String reason
) {
}
