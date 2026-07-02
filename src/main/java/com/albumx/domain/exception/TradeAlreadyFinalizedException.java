package com.albumx.domain.exception;

public class TradeAlreadyFinalizedException extends DomainException {

    public TradeAlreadyFinalizedException() {
        super("TRADE_ALREADY_FINALIZED", "A proposta de troca já foi finalizada.");
    }
}
