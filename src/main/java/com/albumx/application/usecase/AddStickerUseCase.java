package com.albumx.application.usecase;

import com.albumx.application.dto.AddStickerRequest;
import com.albumx.application.dto.CollectionEntryResponse;
import com.albumx.domain.model.CollectionEntry;
import com.albumx.domain.service.CollectionService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddStickerUseCase {

    private final CollectionService collectionService;

    public AddStickerUseCase(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    public CollectionEntryResponse execute(UUID userId, AddStickerRequest request) {
        CollectionEntry entry = collectionService.addSticker(userId, request.stickerNumber());
        return new CollectionEntryResponse(entry.getStickerNumber(), entry.getQuantity());
    }
}
