package com.albumx.infrastructure.web;

import com.albumx.application.dto.CreateTradeRequest;
import com.albumx.application.dto.TradeActionRequest;
import com.albumx.application.dto.TradeProposalResponse;
import com.albumx.application.usecase.AcceptTradeUseCase;
import com.albumx.application.usecase.CreateTradeUseCase;
import com.albumx.application.usecase.GetTradeUseCase;
import com.albumx.application.usecase.ListTradesUseCase;
import com.albumx.application.usecase.RejectTradeUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final CreateTradeUseCase createTradeUseCase;
    private final ListTradesUseCase listTradesUseCase;
    private final GetTradeUseCase getTradeUseCase;
    private final AcceptTradeUseCase acceptTradeUseCase;
    private final RejectTradeUseCase rejectTradeUseCase;

    public TradeController(CreateTradeUseCase createTradeUseCase,
                           ListTradesUseCase listTradesUseCase,
                           GetTradeUseCase getTradeUseCase,
                           AcceptTradeUseCase acceptTradeUseCase,
                           RejectTradeUseCase rejectTradeUseCase) {
        this.createTradeUseCase = createTradeUseCase;
        this.listTradesUseCase = listTradesUseCase;
        this.getTradeUseCase = getTradeUseCase;
        this.acceptTradeUseCase = acceptTradeUseCase;
        this.rejectTradeUseCase = rejectTradeUseCase;
    }

    @PostMapping
    public ResponseEntity<TradeProposalResponse> createTrade(@Valid @RequestBody CreateTradeRequest request) {
        TradeProposalResponse response = createTradeUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<TradeProposalResponse> listTrades(
            @RequestParam(required = false) UUID requesterUserId,
            @RequestParam(required = false) UUID targetUserId) {
        return listTradesUseCase.execute(requesterUserId, targetUserId);
    }

    @GetMapping("/{tradeId}")
    public TradeProposalResponse getTrade(@PathVariable UUID tradeId) {
        return getTradeUseCase.execute(tradeId);
    }

    @PostMapping("/{tradeId}/accept")
    public TradeProposalResponse acceptTrade(@PathVariable UUID tradeId,
                                             @Valid @RequestBody TradeActionRequest request) {
        return acceptTradeUseCase.execute(tradeId, request);
    }

    @PostMapping("/{tradeId}/reject")
    public TradeProposalResponse rejectTrade(@PathVariable UUID tradeId,
                                             @Valid @RequestBody TradeActionRequest request) {
        return rejectTradeUseCase.execute(tradeId, request);
    }
}
