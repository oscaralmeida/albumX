package com.albumx.domain.service;

import com.albumx.domain.model.Album;
import com.albumx.domain.model.User;
import com.albumx.domain.model.UserCollection;
import com.albumx.domain.model.UserRankingPosition;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private TradeProposalRepository tradeProposalRepository;

    private RankingService rankingService;

    private final UUID userXId = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private final UUID userYId = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private final Album album = new Album(100);

    @BeforeEach
    void setUp() {
        CollectionService collectionService = new CollectionService(collectionRepository, userRepository, album);
        rankingService = new RankingService(userRepository, collectionService, tradeProposalRepository, album);
    }

    @Test
    void shouldOrderByCompletionPercentage() {
        User userX = new User(userXId, "X");
        User userY = new User(userYId, "Y");
        when(userRepository.findAll()).thenReturn(List.of(userX, userY));
        when(userRepository.existsById(userXId)).thenReturn(true);
        when(userRepository.existsById(userYId)).thenReturn(true);
        stubCollection(userXId, uniqueStickers(50));
        stubCollection(userYId, uniqueStickers(30));
        when(tradeProposalRepository.countAcceptedByUserId(userXId)).thenReturn(0);
        when(tradeProposalRepository.countAcceptedByUserId(userYId)).thenReturn(0);

        List<UserRankingPosition> ranking = rankingService.getRanking();

        assertThat(ranking.get(0).userId()).isEqualTo(userXId);
        assertThat(ranking.get(1).userId()).isEqualTo(userYId);
        assertThat(ranking.get(0).albumCompletionPercentage()).isEqualTo(50.0);
        assertThat(ranking.get(1).albumCompletionPercentage()).isEqualTo(30.0);
    }

    @Test
    void shouldTieBreakByAcceptedTrades() {
        User userX = new User(userXId, "X");
        User userY = new User(userYId, "Y");
        when(userRepository.findAll()).thenReturn(List.of(userX, userY));
        when(userRepository.existsById(userXId)).thenReturn(true);
        when(userRepository.existsById(userYId)).thenReturn(true);
        stubCollection(userXId, uniqueStickers(50));
        stubCollection(userYId, uniqueStickers(50));
        when(tradeProposalRepository.countAcceptedByUserId(userXId)).thenReturn(5);
        when(tradeProposalRepository.countAcceptedByUserId(userYId)).thenReturn(2);

        List<UserRankingPosition> ranking = rankingService.getRanking();

        assertThat(ranking.get(0).userId()).isEqualTo(userXId);
        assertThat(ranking.get(1).userId()).isEqualTo(userYId);
    }

    @Test
    void shouldReturnZeroPercentForEmptyCollection() {
        User userX = new User(userXId, "X");
        when(userRepository.findAll()).thenReturn(List.of(userX));
        when(userRepository.existsById(userXId)).thenReturn(true);
        when(collectionRepository.findByUserId(userXId)).thenReturn(Optional.empty());
        when(tradeProposalRepository.countAcceptedByUserId(userXId)).thenReturn(0);

        List<UserRankingPosition> ranking = rankingService.getRanking();

        assertThat(ranking).hasSize(1);
        assertThat(ranking.get(0).uniqueStickersCount()).isZero();
        assertThat(ranking.get(0).albumCompletionPercentage()).isZero();
        assertThat(ranking.get(0).acceptedTradesCount()).isZero();
        assertThat(ranking.get(0).position()).isEqualTo(1);
    }

    @Test
    void shouldCountAcceptedTradesForBothParticipants() {
        User userX = new User(userXId, "X");
        when(userRepository.findAll()).thenReturn(List.of(userX));
        when(userRepository.existsById(userXId)).thenReturn(true);
        stubCollection(userXId, uniqueStickers(10));
        when(tradeProposalRepository.countAcceptedByUserId(userXId)).thenReturn(3);

        List<UserRankingPosition> ranking = rankingService.getRanking();

        assertThat(ranking.get(0).acceptedTradesCount()).isEqualTo(3);
    }

    @Test
    void shouldReturnEmptyWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserRankingPosition> ranking = rankingService.getRanking();

        assertThat(ranking).isEmpty();
    }

    @Test
    void shouldNotMutateDataOnRankingQuery() {
        User userX = new User(userXId, "X");
        when(userRepository.findAll()).thenReturn(List.of(userX));
        when(userRepository.existsById(userXId)).thenReturn(true);
        stubCollection(userXId, uniqueStickers(5));
        when(tradeProposalRepository.countAcceptedByUserId(userXId)).thenReturn(1);

        rankingService.getRanking();

        verify(collectionRepository, never()).save(any());
        verify(tradeProposalRepository, never()).save(any());
    }

    private void stubCollection(UUID userId, UserCollection collection) {
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));
    }

    private UserCollection uniqueStickers(int count) {
        UserCollection collection = new UserCollection(userXId);
        for (int i = 1; i <= count; i++) {
            collection.addSticker(i);
        }
        return collection;
    }
}
