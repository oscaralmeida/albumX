package com.albumx.application.usecase;

import com.albumx.application.dto.TradeProposalResponse;
import com.albumx.domain.model.TradeProposal;
import com.albumx.domain.service.TradeService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetTradeUseCase {

    private final TradeService tradeService;

    public GetTradeUseCase(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    public TradeProposalResponse execute(UUID tradeId) {
        TradeProposal proposal = tradeService.getProposal(tradeId);
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
