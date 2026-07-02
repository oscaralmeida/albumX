package com.albumx.infrastructure.web;

import com.albumx.application.dto.UserRankingResponse;
import com.albumx.application.usecase.GetRankingUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final GetRankingUseCase getRankingUseCase;

    public RankingController(GetRankingUseCase getRankingUseCase) {
        this.getRankingUseCase = getRankingUseCase;
    }

    @GetMapping
    public List<UserRankingResponse> getRanking() {
        return getRankingUseCase.execute();
    }
}
