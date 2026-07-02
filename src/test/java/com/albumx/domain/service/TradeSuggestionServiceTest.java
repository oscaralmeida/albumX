package com.albumx.domain.service;

import com.albumx.domain.exception.UserNotFoundException;
import com.albumx.domain.model.TradeSuggestion;
import com.albumx.domain.model.User;
import com.albumx.domain.model.UserCollection;
import com.albumx.domain.repository.CollectionRepository;
import com.albumx.domain.repository.TradeProposalRepository;
import com.albumx.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeSuggestionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private TradeProposalRepository tradeProposalRepository;

    private CollectionService collectionService;
    private TradeSuggestionService suggestionService;
    private TradeSuggestionService suggestionServiceWithProtection;

    private final UUID userAId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID userBId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final User userA = new User(userAId, "Alice");
    private final User userB = new User(userBId, "Bob");

    @BeforeEach
    void setUp() {
        collectionService = new CollectionService(collectionRepository, userRepository, new com.albumx.domain.model.Album(700));
        suggestionService = new TradeSuggestionService(userRepository, collectionService, false);
        suggestionServiceWithProtection = new TradeSuggestionService(userRepository, collectionService, true);
    }

    @Test
    void shouldSuggestMutualBenefitBetweenTwoUsers() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 2, 25, 1));
        stubCollection(userBId, collectionWith(userBId, 25, 2, 50, 1));

        List<TradeSuggestion> suggestions = suggestionService.findSuggestionsForUser(userAId);

        assertThat(suggestions).isNotEmpty();
        assertThat(suggestions).anyMatch(s ->
                s.userAId().equals(userAId)
                        && s.userBId().equals(userBId)
                        && s.stickerOfferedByUserA() == 10
                        && s.stickerOfferedByUserB() == 50);
    }

    @Test
    void shouldReturnEmptyWhenNoMutualBenefit() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 2, 25, 1));
        stubCollection(userBId, collectionWith(userBId, 25, 1));

        List<TradeSuggestion> suggestions = suggestionService.findSuggestionsForUser(userAId);

        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenNoDuplicates() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 1));
        stubCollection(userBId, collectionWith(userBId, 25, 1));

        List<TradeSuggestion> suggestions = suggestionServiceWithProtection.findSuggestionsForUser(userAId);

        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldNotMutateCollectionsOrCreateProposals() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 2, 25, 1));
        stubCollection(userBId, collectionWith(userBId, 25, 2, 50, 1));

        suggestionService.findSuggestionsForUser(userAId);

        verify(collectionRepository, never()).save(any());
        verify(tradeProposalRepository, never()).save(any());
    }

    @Test
    void shouldThrowUserNotFoundWhenUserDoesNotExist() {
        when(userRepository.existsById(userAId)).thenReturn(false);

        assertThatThrownBy(() -> suggestionService.findSuggestionsForUser(userAId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldRejectSameStickerNumber() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 2));
        stubCollection(userBId, collectionWith(userBId, 10, 2, 25, 1));

        List<TradeSuggestion> suggestions = suggestionService.findSuggestionsForUser(userAId);

        assertThat(suggestions).noneMatch(s -> s.stickerOfferedByUserA() == s.stickerOfferedByUserB());
    }

    @Test
    void shouldRespectSingleStickerProtectionWhenEnabled() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 1, 25, 1));
        stubCollection(userBId, collectionWith(userBId, 25, 1, 50, 1));

        List<TradeSuggestion> suggestions = suggestionServiceWithProtection.findSuggestionsForUser(userAId);

        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldAllowOfferWhenProtectionDisabled() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 1, 25, 1));
        stubCollection(userBId, collectionWith(userBId, 25, 1, 50, 1));

        List<TradeSuggestion> suggestions = suggestionService.findSuggestionsForUser(userAId);

        assertThat(suggestions).isNotEmpty();
    }

    @Test
    void shouldInvertPerspectiveWhenPartnerQueries() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 2));
        stubCollection(userBId, collectionWith(userBId, 25, 2));

        List<TradeSuggestion> fromA = suggestionService.findSuggestionsForUser(userAId);
        List<TradeSuggestion> fromB = suggestionService.findSuggestionsForUser(userBId);

        assertThat(fromA).hasSize(1);
        assertThat(fromB).hasSize(1);
        assertThat(fromA.get(0).userAId()).isEqualTo(userAId);
        assertThat(fromB.get(0).userAId()).isEqualTo(userBId);
        assertThat(fromA.get(0).stickerOfferedByUserA()).isEqualTo(10);
        assertThat(fromB.get(0).stickerOfferedByUserA()).isEqualTo(25);
    }

    @Test
    void shouldExcludeSelfTrade() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 2, 25, 1));
        stubCollection(userBId, collectionWith(userBId, 25, 2, 50, 1));

        List<TradeSuggestion> suggestions = suggestionService.findSuggestionsForUser(userAId);

        assertThat(suggestions).allMatch(s -> !s.userAId().equals(s.userBId()));
    }

    @Test
    void shouldNotDuplicateSameOpportunityInSingleQuery() {
        stubUsers(userA, userB);
        stubCollection(userAId, collectionWith(userAId, 10, 2, 25, 1));
        stubCollection(userBId, collectionWith(userBId, 25, 2, 50, 1));

        List<TradeSuggestion> suggestions = suggestionService.findSuggestionsForUser(userAId);

        long count = suggestions.stream()
                .filter(s -> s.stickerOfferedByUserA() == 10 && s.stickerOfferedByUserB() == 50)
                .count();
        assertThat(count).isEqualTo(1);
    }

    private void stubUsers(User... users) {
        when(userRepository.findAll()).thenReturn(List.of(users));
        for (User user : users) {
            when(userRepository.existsById(user.getId())).thenReturn(true);
        }
    }

    private void stubCollection(UUID userId, UserCollection collection) {
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));
    }

    private UserCollection collectionWith(UUID userId, int... stickerQtyPairs) {
        UserCollection collection = new UserCollection(userId);
        for (int i = 0; i < stickerQtyPairs.length; i += 2) {
            int sticker = stickerQtyPairs[i];
            int qty = stickerQtyPairs[i + 1];
            for (int j = 0; j < qty; j++) {
                collection.addSticker(sticker);
            }
        }
        return collection;
    }
}
