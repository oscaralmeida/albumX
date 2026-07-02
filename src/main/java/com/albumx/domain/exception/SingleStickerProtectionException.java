package com.albumx.domain.exception;

public class SingleStickerProtectionException extends DomainException {

    public SingleStickerProtectionException(int stickerNumber) {
        super("SINGLE_STICKER_PROTECTED",
                "Não é permitido trocar a única unidade da figurinha " + stickerNumber + ".");
    }
}
