package com.albumx.domain.model;

/**
 * Status da proposta de troca.
 * MVP utiliza apenas PROPOSED; ACCEPTED e REJECTED reservados para Evolução 1.
 */
public enum TradeStatus {
    PROPOSED,
    ACCEPTED,
    REJECTED
}
