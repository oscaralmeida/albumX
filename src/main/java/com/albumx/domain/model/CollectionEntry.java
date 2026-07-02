package com.albumx.domain.model;

public class CollectionEntry {

    private final int stickerNumber;
    private final int quantity;

    public CollectionEntry(int stickerNumber, int quantity) {
        this.stickerNumber = stickerNumber;
        this.quantity = quantity;
    }

    public int getStickerNumber() {
        return stickerNumber;
    }

    public int getQuantity() {
        return quantity;
    }
}
