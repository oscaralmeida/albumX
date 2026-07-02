package com.albumx.domain.model;

import java.time.Instant;
import java.util.UUID;

public class TradeProposal {

    private final UUID id;
    private final UUID requesterUserId;
    private final UUID targetUserId;
    private final int offeredStickerNumber;
    private final int requestedStickerNumber;
    private final TradeStatus status;
    private final Instant createdAt;

    public TradeProposal(UUID id,
                         UUID requesterUserId,
                         UUID targetUserId,
                         int offeredStickerNumber,
                         int requestedStickerNumber,
                         TradeStatus status,
                         Instant createdAt) {
        this.id = id;
        this.requesterUserId = requesterUserId;
        this.targetUserId = targetUserId;
        this.offeredStickerNumber = offeredStickerNumber;
        this.requestedStickerNumber = requestedStickerNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRequesterUserId() {
        return requesterUserId;
    }

    public UUID getTargetUserId() {
        return targetUserId;
    }

    public int getOfferedStickerNumber() {
        return offeredStickerNumber;
    }

    public int getRequestedStickerNumber() {
        return requestedStickerNumber;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public TradeProposal accept() {
        return withStatus(TradeStatus.ACCEPTED);
    }

    public TradeProposal reject() {
        return withStatus(TradeStatus.REJECTED);
    }

    private TradeProposal withStatus(TradeStatus newStatus) {
        return new TradeProposal(
                id,
                requesterUserId,
                targetUserId,
                offeredStickerNumber,
                requestedStickerNumber,
                newStatus,
                createdAt
        );
    }
}
