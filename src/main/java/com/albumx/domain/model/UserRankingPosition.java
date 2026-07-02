package com.albumx.domain.model;

import java.util.UUID;

public record UserRankingPosition(
        UUID userId,
        String userName,
        int uniqueStickersCount,
        double albumCompletionPercentage,
        int acceptedTradesCount,
        int position
) {
}
