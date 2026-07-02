package com.albumx.application.usecase;

import com.albumx.application.dto.CollectionEntryResponse;
import com.albumx.domain.model.CollectionEntry;
import com.albumx.domain.service.CollectionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GetDuplicatesUseCase {

    private final CollectionService collectionService;

    public GetDuplicatesUseCase(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    public List<CollectionEntryResponse> execute(UUID userId) {
        return collectionService.getDuplicates(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private CollectionEntryResponse toResponse(CollectionEntry entry) {
        return new CollectionEntryResponse(entry.getStickerNumber(), entry.getQuantity());
    }
}
