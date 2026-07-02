package com.albumx.domain.model;

import java.util.UUID;

public record TradeSuggestion(
        UUID userAId,
        UUID userBId,
        int stickerOfferedByUserA,
        int stickerOfferedByUserB,
        String reason
) {
}
