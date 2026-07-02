package com.albumx.application.usecase;

import com.albumx.application.dto.TradeProposalResponse;
import com.albumx.domain.model.TradeProposal;
import com.albumx.domain.service.TradeService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ListTradesUseCase {

    private final TradeService tradeService;

    public ListTradesUseCase(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    public List<TradeProposalResponse> execute(UUID requesterUserId, UUID targetUserId) {
        return tradeService.listProposals(requesterUserId, targetUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    private TradeProposalResponse toResponse(TradeProposal proposal) {
        return new TradeProposalResponse(
                proposal.getId(),
                proposal.getRequesterUserId(),
                proposal.getTargetUserId(),
                proposal.getOfferedStickerNumber(),
                proposal.getRequestedStickerNumber(),
                proposal.getStatus(),
                proposal.getCreatedAt()
        );
    }
}
