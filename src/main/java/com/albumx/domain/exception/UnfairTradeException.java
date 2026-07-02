package com.albumx.domain.exception;

public class UnfairTradeException extends DomainException {

    public UnfairTradeException() {
        super("UNFAIR_TRADE", "A troca deve envolver figurinhas com números distintos.");
    }
}
