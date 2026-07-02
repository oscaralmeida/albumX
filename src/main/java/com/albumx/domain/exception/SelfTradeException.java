package com.albumx.domain.exception;

public class SelfTradeException extends DomainException {

    public SelfTradeException() {
        super("SELF_TRADE", "Não é possível criar proposta de troca para si mesmo.");
    }
}
