package com.albumx.application.usecase;

import com.albumx.application.dto.TradeActionRequest;
import com.albumx.application.dto.TradeProposalResponse;
import com.albumx.domain.model.TradeProposal;
import com.albumx.domain.service.TradeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RejectTradeUseCase {

    private final TradeService tradeService;

    public RejectTradeUseCase(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Transactional
    public TradeProposalResponse execute(UUID tradeId, TradeActionRequest request) {
        TradeProposal proposal = tradeService.rejectProposal(tradeId, request.targetUserId());
        return toResponse(proposal);
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
