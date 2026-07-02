package com.albumx.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
        name = "collection_entry",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "sticker_number"})
)
public class CollectionEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "sticker_number", nullable = false)
    private int stickerNumber;

    @Column(nullable = false)
    private int quantity;

    protected CollectionEntryEntity() {
    }

    public CollectionEntryEntity(UUID userId, int stickerNumber, int quantity) {
        this.userId = userId;
        this.stickerNumber = stickerNumber;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getStickerNumber() {
        return stickerNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
