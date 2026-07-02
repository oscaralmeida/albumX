package com.albumx.infrastructure.web;

import com.albumx.application.dto.TradeSuggestionResponse;
import com.albumx.application.usecase.GetAllTradeSuggestionsUseCase;
import com.albumx.application.usecase.GetTradeSuggestionsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TradeSuggestionController {

    private final GetTradeSuggestionsUseCase getTradeSuggestionsUseCase;
    private final GetAllTradeSuggestionsUseCase getAllTradeSuggestionsUseCase;

    public TradeSuggestionController(GetTradeSuggestionsUseCase getTradeSuggestionsUseCase,
                                     GetAllTradeSuggestionsUseCase getAllTradeSuggestionsUseCase) {
        this.getTradeSuggestionsUseCase = getTradeSuggestionsUseCase;
        this.getAllTradeSuggestionsUseCase = getAllTradeSuggestionsUseCase;
    }

    @GetMapping("/users/{userId}/trade-suggestions")
    public List<TradeSuggestionResponse> getSuggestionsForUser(@PathVariable("userId") UUID userId) {
        return getTradeSuggestionsUseCase.execute(userId);
    }

    @GetMapping("/trade-suggestions")
    public List<TradeSuggestionResponse> getAllSuggestions() {
        return getAllTradeSuggestionsUseCase.execute();
    }
}
