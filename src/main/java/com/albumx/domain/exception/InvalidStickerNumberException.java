package com.albumx.domain.exception;

public class InvalidStickerNumberException extends DomainException {

    public InvalidStickerNumberException(int stickerNumber, int max) {
        super("INVALID_STICKER_NUMBER",
                "Número de figurinha inválido: " + stickerNumber + ". Deve estar entre 1 e " + max + ".");
    }
}
