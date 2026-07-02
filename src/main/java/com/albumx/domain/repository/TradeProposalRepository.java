package com.albumx.domain.repository;

import com.albumx.domain.model.TradeProposal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TradeProposalRepository {

    TradeProposal save(TradeProposal proposal);

    List<TradeProposal> findAll(UUID requesterUserId, UUID targetUserId);

    Optional<TradeProposal> findById(UUID id);

    int countAcceptedByUserId(UUID userId);
}
