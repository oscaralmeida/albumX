package com.albumx.domain.exception;

public class StickerNotOwnedException extends DomainException {

    public StickerNotOwnedException(int stickerNumber) {
        this(stickerNumber, true);
    }

    public StickerNotOwnedException(int stickerNumber, boolean requester) {
        super("STICKER_NOT_OWNED",
                (requester ? "O solicitante" : "O destinatário")
                        + " não possui a figurinha " + stickerNumber + " na coleção.");
    }
}
