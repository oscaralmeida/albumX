package com.albumx.application.usecase;

import com.albumx.application.dto.CreateTradeRequest;
import com.albumx.application.dto.TradeProposalResponse;
import com.albumx.domain.model.TradeProposal;
import com.albumx.domain.service.TradeService;
import org.springframework.stereotype.Component;

@Component
public class CreateTradeUseCase {

    private final TradeService tradeService;

    public CreateTradeUseCase(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    public TradeProposalResponse execute(CreateTradeRequest request) {
        TradeProposal proposal = tradeService.createProposal(
                request.requesterUserId(),
                request.targetUserId(),
                request.offeredStickerNumber(),
                request.requestedStickerNumber()
        );
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
