package com.albumx.domain.service;

import com.albumx.domain.exception.UserNotFoundException;
import com.albumx.domain.model.Album;
import com.albumx.domain.model.CollectionEntry;
import com.albumx.domain.model.UserCollection;
import com.albumx.domain.repository.CollectionRepository;
import com.albumx.domain.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final Album album;

    public CollectionService(CollectionRepository collectionRepository,
                             UserRepository userRepository,
                             Album album) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.album = album;
    }

    public CollectionEntry addSticker(UUID userId, int stickerNumber) {
        ensureUserExists(userId);
        album.validateStickerNumber(stickerNumber);

        UserCollection collection = getOrCreateCollection(userId);
        collection.addSticker(stickerNumber);
        collectionRepository.save(collection);

        return new CollectionEntry(stickerNumber, collection.getQuantity(stickerNumber));
    }

    public UserCollection getCollection(UUID userId) {
        ensureUserExists(userId);
        return getOrCreateCollection(userId);
    }

    public List<CollectionEntry> getDuplicates(UUID userId) {
        ensureUserExists(userId);
        return getOrCreateCollection(userId).getDuplicates();
    }

    public boolean hasSticker(UUID userId, int stickerNumber) {
        return getOrCreateCollection(userId).hasSticker(stickerNumber);
    }

    public CollectionEntry removeSticker(UUID userId, int stickerNumber) {
        ensureUserExists(userId);
        album.validateStickerNumber(stickerNumber);

        UserCollection collection = getOrCreateCollection(userId);
        collection.removeSticker(stickerNumber);
        collectionRepository.save(collection);

        return new CollectionEntry(stickerNumber, collection.getQuantity(stickerNumber));
    }

    public int getQuantity(UUID userId, int stickerNumber) {
        ensureUserExists(userId);
        return getOrCreateCollection(userId).getQuantity(stickerNumber);
    }

    public boolean hasDuplicate(UUID userId, int stickerNumber) {
        ensureUserExists(userId);
        return getOrCreateCollection(userId).hasDuplicate(stickerNumber);
    }

    public int getUniqueStickerCount(UUID userId) {
        ensureUserExists(userId);
        return getCollectionReadOnly(userId).getUniqueStickerNumbers().size();
    }

    public List<Integer> getMissingStickerNumbers(UUID userId) {
        ensureUserExists(userId);
        return getCollectionReadOnly(userId).getMissingStickerNumbers(album.getStickerCount());
    }

    public boolean canOfferSticker(UUID userId, int stickerNumber, boolean protectSingleSticker) {
        ensureUserExists(userId);
        int quantity = getCollectionReadOnly(userId).getQuantity(stickerNumber);
        if (protectSingleSticker) {
            return quantity >= 2;
        }
        return quantity >= 1;
    }

    public UserCollection getCollectionReadOnly(UUID userId) {
        ensureUserExists(userId);
        return collectionRepository.findByUserId(userId)
                .orElse(new UserCollection(userId));
    }

    private UserCollection getOrCreateCollection(UUID userId) {
        return collectionRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserCollection empty = new UserCollection(userId);
                    return collectionRepository.save(empty);
                });
    }

    private void ensureUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
