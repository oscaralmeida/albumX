package com.albumx.domain.model;

import com.albumx.domain.exception.InvalidStickerNumberException;

public class Album {

    private final int stickerCount;

    public Album(int stickerCount) {
        this.stickerCount = stickerCount;
    }

    public int getStickerCount() {
        return stickerCount;
    }

    public void validateStickerNumber(int stickerNumber) {
        if (stickerNumber < 1 || stickerNumber > stickerCount) {
            throw new InvalidStickerNumberException(stickerNumber, stickerCount);
        }
    }
}
