package com.albumx.application.usecase;

import com.albumx.application.dto.UserRankingResponse;
import com.albumx.domain.model.UserRankingPosition;
import com.albumx.domain.service.RankingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRankingUseCase {

    private final RankingService rankingService;

    public GetRankingUseCase(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    public List<UserRankingResponse> execute() {
        return rankingService.getRanking().stream()
                .map(this::toResponse)
                .toList();
    }

    private UserRankingResponse toResponse(UserRankingPosition position) {
        return new UserRankingResponse(
                position.position(),
                position.userId(),
                position.userName(),
                position.uniqueStickersCount(),
                position.albumCompletionPercentage(),
                position.acceptedTradesCount()
        );
    }
}
