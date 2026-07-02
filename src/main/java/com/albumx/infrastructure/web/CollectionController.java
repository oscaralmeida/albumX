package com.albumx.infrastructure.web;

import com.albumx.application.dto.AddStickerRequest;
import com.albumx.application.dto.CollectionEntryResponse;
import com.albumx.application.dto.CollectionResponse;
import com.albumx.application.usecase.AddStickerUseCase;
import com.albumx.application.usecase.GetCollectionUseCase;
import com.albumx.application.usecase.GetDuplicatesUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/collection")
public class CollectionController {

    private final AddStickerUseCase addStickerUseCase;
    private final GetCollectionUseCase getCollectionUseCase;
    private final GetDuplicatesUseCase getDuplicatesUseCase;

    public CollectionController(AddStickerUseCase addStickerUseCase,
                                GetCollectionUseCase getCollectionUseCase,
                                GetDuplicatesUseCase getDuplicatesUseCase) {
        this.addStickerUseCase = addStickerUseCase;
        this.getCollectionUseCase = getCollectionUseCase;
        this.getDuplicatesUseCase = getDuplicatesUseCase;
    }

    @PostMapping("/stickers")
    public CollectionEntryResponse addSticker(@PathVariable UUID userId,
                                              @Valid @RequestBody AddStickerRequest request) {
        return addStickerUseCase.execute(userId, request);
    }

    @GetMapping
    public CollectionResponse getCollection(@PathVariable UUID userId) {
        return getCollectionUseCase.execute(userId);
    }

    @GetMapping("/duplicates")
    public List<CollectionEntryResponse> getDuplicates(@PathVariable UUID userId) {
        return getDuplicatesUseCase.execute(userId);
    }
}
