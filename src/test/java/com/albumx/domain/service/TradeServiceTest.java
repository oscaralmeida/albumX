package com.albumx.domain.service;

import com.albumx.domain.exception.SelfTradeException;
import com.albumx.domain.exception.SingleStickerProtectionException;
import com.albumx.domain.exception.StickerNotOwnedException;
import com.albumx.domain.exception.TradeAlreadyFinalizedException;
import com.albumx.domain.exception.TradeNotFoundException;
import com.albumx.domain.exception.UnauthorizedTradeActionException;
import com.albumx.domain.exception.UnfairTradeException;
import com.albumx.domain.exception.UserNotFoundException;
import com.albumx.domain.model.Album;
import com.albumx.domain.model.TradeProposal;
import com.albumx.domain.model.TradeStatus;
import com.albumx.domain.repository.TradeProposalRepository;
import com.albumx.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private TradeProposalRepository tradeProposalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollectionService collectionService;

    private TradeService tradeService;
    private TradeService tradeServiceWithProtection;
    private final UUID requesterId = UUID.randomUUID();
    private final UUID targetId = UUID.randomUUID();
    private final UUID tradeId = UUID.randomUUID();
    private final Album album = new Album(700);

    @BeforeEach
    void setUp() {
        tradeService = new TradeService(tradeProposalRepository, userRepository, collectionService, album, false);
        tradeServiceWithProtection = new TradeService(tradeProposalRepository, userRepository, collectionService, album, true);
    }

    @Test
    void shouldCreateProposalWithProposedStatus() {
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(userRepository.existsById(targetId)).thenReturn(true);
        when(collectionService.hasSticker(requesterId, 10)).thenReturn(true);
        when(tradeProposalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TradeProposal proposal = tradeService.createProposal(requesterId, targetId, 10, 50);

        assertThat(proposal.getStatus()).isEqualTo(TradeStatus.PROPOSED);
        assertThat(proposal.getOfferedStickerNumber()).isEqualTo(10);
        assertThat(proposal.getRequestedStickerNumber()).isEqualTo(50);
        verify(collectionService, never()).removeSticker(any(), eq(10));
    }

    @Test
    void shouldRejectWhenRequesterDoesNotOwnSticker() {
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(userRepository.existsById(targetId)).thenReturn(true);
        when(collectionService.hasSticker(requesterId, 99)).thenReturn(false);

        assertThatThrownBy(() -> tradeService.createProposal(requesterId, targetId, 99, 50))
                .isInstanceOf(StickerNotOwnedException.class);
    }

    @Test
    void shouldRejectSelfTrade() {
        assertThatThrownBy(() -> tradeService.createProposal(requesterId, requesterId, 10, 50))
                .isInstanceOf(SelfTradeException.class);
    }

    @Test
    void shouldRejectWhenUserNotFound() {
        when(userRepository.existsById(requesterId)).thenReturn(false);

        assertThatThrownBy(() -> tradeService.createProposal(requesterId, targetId, 10, 50))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldAcceptValidProposal() {
        TradeProposal proposed = proposedTrade(10, 50);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));
        when(collectionService.hasSticker(requesterId, 10)).thenReturn(true);
        when(collectionService.hasSticker(targetId, 50)).thenReturn(true);
        when(tradeProposalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TradeProposal result = tradeService.acceptProposal(tradeId, targetId);

        assertThat(result.getStatus()).isEqualTo(TradeStatus.ACCEPTED);
    }

    @Test
    void shouldUpdateRequesterCollectionOnAccept() {
        TradeProposal proposed = proposedTrade(10, 50);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));
        when(collectionService.hasSticker(requesterId, 10)).thenReturn(true);
        when(collectionService.hasSticker(targetId, 50)).thenReturn(true);
        when(tradeProposalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        tradeService.acceptProposal(tradeId, targetId);

        verify(collectionService).removeSticker(requesterId, 10);
        verify(collectionService).addSticker(requesterId, 50);
    }

    @Test
    void shouldUpdateTargetCollectionOnAccept() {
        TradeProposal proposed = proposedTrade(10, 50);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));
        when(collectionService.hasSticker(requesterId, 10)).thenReturn(true);
        when(collectionService.hasSticker(targetId, 50)).thenReturn(true);
        when(tradeProposalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        tradeService.acceptProposal(tradeId, targetId);

        verify(collectionService).removeSticker(targetId, 50);
        verify(collectionService).addSticker(targetId, 10);
    }

    @Test
    void shouldRejectValidProposal() {
        TradeProposal proposed = proposedTrade(10, 50);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));
        when(tradeProposalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TradeProposal result = tradeService.rejectProposal(tradeId, targetId);

        assertThat(result.getStatus()).isEqualTo(TradeStatus.REJECTED);
    }

    @Test
    void shouldNotMutateCollectionsOnReject() {
        TradeProposal proposed = proposedTrade(10, 50);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));
        when(tradeProposalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        tradeService.rejectProposal(tradeId, targetId);

        verify(collectionService, never()).removeSticker(any(), anyInt());
        verify(collectionService, never()).addSticker(any(), anyInt());
    }

    @Test
    void shouldRejectAcceptWhenAlreadyAccepted() {
        TradeProposal accepted = new TradeProposal(
                tradeId, requesterId, targetId, 10, 50, TradeStatus.ACCEPTED, Instant.now());
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(accepted));

        assertThatThrownBy(() -> tradeService.acceptProposal(tradeId, targetId))
                .isInstanceOf(TradeAlreadyFinalizedException.class);
    }

    @Test
    void shouldRejectRejectWhenAlreadyAccepted() {
        TradeProposal accepted = new TradeProposal(
                tradeId, requesterId, targetId, 10, 50, TradeStatus.ACCEPTED, Instant.now());
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(accepted));

        assertThatThrownBy(() -> tradeService.rejectProposal(tradeId, targetId))
                .isInstanceOf(TradeAlreadyFinalizedException.class);
    }

    @Test
    void shouldRejectAcceptWhenAlreadyRejected() {
        TradeProposal rejected = new TradeProposal(
                tradeId, requesterId, targetId, 10, 50, TradeStatus.REJECTED, Instant.now());
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(rejected));

        assertThatThrownBy(() -> tradeService.acceptProposal(tradeId, targetId))
                .isInstanceOf(TradeAlreadyFinalizedException.class);
    }

    @Test
    void shouldRejectAcceptWhenRequesterLacksOfferedSticker() {
        TradeProposal proposed = proposedTrade(10, 50);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));
        when(collectionService.hasSticker(requesterId, 10)).thenReturn(false);

        assertThatThrownBy(() -> tradeService.acceptProposal(tradeId, targetId))
                .isInstanceOf(StickerNotOwnedException.class);

        verify(collectionService, never()).removeSticker(any(), anyInt());
    }

    @Test
    void shouldRejectAcceptWhenTargetLacksRequestedSticker() {
        TradeProposal proposed = proposedTrade(10, 50);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));
        when(collectionService.hasSticker(requesterId, 10)).thenReturn(true);
        when(collectionService.hasSticker(targetId, 50)).thenReturn(false);

        assertThatThrownBy(() -> tradeService.acceptProposal(tradeId, targetId))
                .isInstanceOf(StickerNotOwnedException.class);

        verify(collectionService, never()).removeSticker(any(), anyInt());
    }

    @Test
    void shouldRejectUnfairTrade() {
        TradeProposal proposed = proposedTrade(10, 10);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));

        assertThatThrownBy(() -> tradeService.acceptProposal(tradeId, targetId))
                .isInstanceOf(UnfairTradeException.class);

        verify(collectionService, never()).removeSticker(any(), anyInt());
    }

    @Test
    void shouldRejectSingleStickerWhenProtectionEnabled() {
        TradeProposal proposed = proposedTrade(10, 50);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));
        when(collectionService.hasSticker(requesterId, 10)).thenReturn(true);
        when(collectionService.hasSticker(targetId, 50)).thenReturn(true);
        when(collectionService.getQuantity(requesterId, 10)).thenReturn(1);

        assertThatThrownBy(() -> tradeServiceWithProtection.acceptProposal(tradeId, targetId))
                .isInstanceOf(SingleStickerProtectionException.class);

        verify(collectionService, never()).removeSticker(any(), anyInt());
    }

    @Test
    void shouldAllowSingleStickerWhenProtectionDisabled() {
        TradeProposal proposed = proposedTrade(10, 50);
        when(tradeProposalRepository.findById(tradeId)).thenReturn(Optional.of(proposed));
        when(collectionService.hasSticker(requesterId, 10)).thenReturn(true);
        when(collectionService.hasSticker(targetId, 50)).thenReturn(true);
        when(tradeProposalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TradeProposal result = tradeService.acceptProposal(tradeId, targetId);

        assertThat(result.getStatus()).isEqualTo(TradeStatus.ACCEPTED);
    }

    private TradeProposal proposedTrade(int offered, int requested) {
        return new TradeProposal(
                tradeId, requesterId, targetId, offered, requested, TradeStatus.PROPOSED, Instant.now());
    }
}
