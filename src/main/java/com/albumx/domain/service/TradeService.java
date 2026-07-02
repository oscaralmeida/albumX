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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class TradeService {

    private final TradeProposalRepository tradeProposalRepository;
    private final UserRepository userRepository;
    private final CollectionService collectionService;
    private final Album album;
    private final boolean protectSingleSticker;

    public TradeService(TradeProposalRepository tradeProposalRepository,
                        UserRepository userRepository,
                        CollectionService collectionService,
                        Album album,
                        boolean protectSingleSticker) {
        this.tradeProposalRepository = tradeProposalRepository;
        this.userRepository = userRepository;
        this.collectionService = collectionService;
        this.album = album;
        this.protectSingleSticker = protectSingleSticker;
    }

    public TradeProposal createProposal(UUID requesterUserId,
                                        UUID targetUserId,
                                        int offeredStickerNumber,
                                        int requestedStickerNumber) {
        if (requesterUserId.equals(targetUserId)) {
            throw new SelfTradeException();
        }

        ensureUserExists(requesterUserId);
        ensureUserExists(targetUserId);

        album.validateStickerNumber(offeredStickerNumber);
        album.validateStickerNumber(requestedStickerNumber);

        if (!collectionService.hasSticker(requesterUserId, offeredStickerNumber)) {
            throw new StickerNotOwnedException(offeredStickerNumber);
        }

        TradeProposal proposal = new TradeProposal(
                UUID.randomUUID(),
                requesterUserId,
                targetUserId,
                offeredStickerNumber,
                requestedStickerNumber,
                TradeStatus.PROPOSED,
                Instant.now()
        );

        return tradeProposalRepository.save(proposal);
    }

    public List<TradeProposal> listProposals(UUID requesterUserId, UUID targetUserId) {
        return tradeProposalRepository.findAll(requesterUserId, targetUserId);
    }

    public TradeProposal getProposal(UUID tradeId) {
        return tradeProposalRepository.findById(tradeId)
                .orElseThrow(() -> new TradeNotFoundException(tradeId));
    }

    public TradeProposal acceptProposal(UUID tradeId, UUID targetUserId) {
        TradeProposal proposal = getProposal(tradeId);
        validateTargetCanAct(proposal, targetUserId);
        validateFairTrade(proposal);
        validateStickerOwnershipForAccept(proposal);
        validateSingleStickerProtection(proposal);

        UUID requesterId = proposal.getRequesterUserId();
        int offered = proposal.getOfferedStickerNumber();
        int requested = proposal.getRequestedStickerNumber();

        collectionService.removeSticker(requesterId, offered);
        collectionService.addSticker(requesterId, requested);
        collectionService.removeSticker(targetUserId, requested);
        collectionService.addSticker(targetUserId, offered);

        return tradeProposalRepository.save(proposal.accept());
    }

    public TradeProposal rejectProposal(UUID tradeId, UUID targetUserId) {
        TradeProposal proposal = getProposal(tradeId);
        validateTargetCanAct(proposal, targetUserId);
        return tradeProposalRepository.save(proposal.reject());
    }

    private void validateTargetCanAct(TradeProposal proposal, UUID targetUserId) {
        if (proposal.getStatus() != TradeStatus.PROPOSED) {
            throw new TradeAlreadyFinalizedException();
        }
        if (!proposal.getTargetUserId().equals(targetUserId)) {
            throw new UnauthorizedTradeActionException();
        }
    }

    private void validateFairTrade(TradeProposal proposal) {
        if (proposal.getOfferedStickerNumber() == proposal.getRequestedStickerNumber()) {
            throw new UnfairTradeException();
        }
    }

    private void validateStickerOwnershipForAccept(TradeProposal proposal) {
        UUID requesterId = proposal.getRequesterUserId();
        UUID targetId = proposal.getTargetUserId();
        int offered = proposal.getOfferedStickerNumber();
        int requested = proposal.getRequestedStickerNumber();

        if (!collectionService.hasSticker(requesterId, offered)) {
            throw new StickerNotOwnedException(offered, true);
        }
        if (!collectionService.hasSticker(targetId, requested)) {
            throw new StickerNotOwnedException(requested, false);
        }
    }

    private void validateSingleStickerProtection(TradeProposal proposal) {
        if (!protectSingleSticker) {
            return;
        }
        UUID requesterId = proposal.getRequesterUserId();
        UUID targetId = proposal.getTargetUserId();
        int offered = proposal.getOfferedStickerNumber();
        int requested = proposal.getRequestedStickerNumber();

        if (collectionService.getQuantity(requesterId, offered) <= 1) {
            throw new SingleStickerProtectionException(offered);
        }
        if (collectionService.getQuantity(targetId, requested) <= 1) {
            throw new SingleStickerProtectionException(requested);
        }
    }

    private void ensureUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
