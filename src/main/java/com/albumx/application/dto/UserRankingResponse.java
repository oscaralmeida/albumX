package com.albumx.application.dto;

import java.util.UUID;

public record UserRankingResponse(
        int position,
        UUID userId,
        String userName,
        int uniqueStickersCount,
        double albumCompletionPercentage,
        int acceptedTradesCount
) {
}
