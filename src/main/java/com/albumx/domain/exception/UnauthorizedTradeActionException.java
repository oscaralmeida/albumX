package com.albumx.domain.exception;

public class UnauthorizedTradeActionException extends DomainException {

    public UnauthorizedTradeActionException() {
        super("UNAUTHORIZED_TRADE_ACTION", "Apenas o destinatário pode aceitar ou recusar a proposta.");
    }
}
