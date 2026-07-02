package com.albumx.domain.exception;

import java.util.UUID;

public class TradeNotFoundException extends DomainException {

    public TradeNotFoundException(UUID tradeId) {
        super("TRADE_NOT_FOUND", "Proposta de troca não encontrada: " + tradeId);
    }
}
