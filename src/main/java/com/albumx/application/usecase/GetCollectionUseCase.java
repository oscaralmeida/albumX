package com.albumx.application.usecase;

import com.albumx.application.dto.CollectionEntryResponse;
import com.albumx.application.dto.CollectionResponse;
import com.albumx.domain.model.CollectionEntry;
import com.albumx.domain.model.UserCollection;
import com.albumx.domain.service.CollectionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GetCollectionUseCase {

    private final CollectionService collectionService;

    public GetCollectionUseCase(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    public CollectionResponse execute(UUID userId) {
        UserCollection collection = collectionService.getCollection(userId);
        List<CollectionEntryResponse> entries = collection.getAllEntries().stream()
                .map(this::toResponse)
                .toList();
        return new CollectionResponse(userId, entries);
    }

    private CollectionEntryResponse toResponse(CollectionEntry entry) {
        return new CollectionEntryResponse(entry.getStickerNumber(), entry.getQuantity());
    }
}
