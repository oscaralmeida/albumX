package com.albumx.infrastructure.persistence;

import com.albumx.domain.model.TradeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trade_proposals")
public class TradeProposalEntity {

    @Id
    private UUID id;

    @Column(name = "requester_user_id", nullable = false)
    private UUID requesterUserId;

    @Column(name = "target_user_id", nullable = false)
    private UUID targetUserId;

    @Column(name = "offered_sticker_number", nullable = false)
    private int offeredStickerNumber;

    @Column(name = "requested_sticker_number", nullable = false)
    private int requestedStickerNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TradeProposalEntity() {
    }

    public TradeProposalEntity(UUID id,
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
}
