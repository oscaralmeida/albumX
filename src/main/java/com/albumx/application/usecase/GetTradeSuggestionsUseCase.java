package com.albumx.application.usecase;

import com.albumx.application.dto.TradeSuggestionResponse;
import com.albumx.domain.model.TradeSuggestion;
import com.albumx.domain.service.TradeSuggestionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GetTradeSuggestionsUseCase {

    private final TradeSuggestionService tradeSuggestionService;

    public GetTradeSuggestionsUseCase(TradeSuggestionService tradeSuggestionService) {
        this.tradeSuggestionService = tradeSuggestionService;
    }

    public List<TradeSuggestionResponse> execute(UUID userId) {
        return tradeSuggestionService.findSuggestionsForUser(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private TradeSuggestionResponse toResponse(TradeSuggestion suggestion) {
        return new TradeSuggestionResponse(
                suggestion.userAId(),
                suggestion.userBId(),
                suggestion.stickerOfferedByUserA(),
                suggestion.stickerOfferedByUserB(),
                suggestion.reason()
        );
    }
}
