package com.albumx.domain.model;

import com.albumx.domain.exception.StickerNotOwnedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserCollection {

    private final UUID userId;
    private final Map<Integer, Integer> entries;

    public UserCollection(UUID userId) {
        this(userId, new HashMap<>());
    }

    public UserCollection(UUID userId, Map<Integer, Integer> entries) {
        this.userId = userId;
        this.entries = new HashMap<>(entries);
    }

    public UUID getUserId() {
        return userId;
    }

    public Map<Integer, Integer> getEntries() {
        return Map.copyOf(entries);
    }

    public void addSticker(int stickerNumber) {
        entries.merge(stickerNumber, 1, Integer::sum);
    }

    public void removeSticker(int stickerNumber) {
        int current = entries.getOrDefault(stickerNumber, 0);
        if (current <= 0) {
            throw new StickerNotOwnedException(stickerNumber);
        }
        if (current == 1) {
            entries.remove(stickerNumber);
        } else {
            entries.put(stickerNumber, current - 1);
        }
    }

    public boolean hasDuplicate(int stickerNumber) {
        return getQuantity(stickerNumber) > 1;
    }

    public int getQuantity(int stickerNumber) {
        return entries.getOrDefault(stickerNumber, 0);
    }

    public boolean hasSticker(int stickerNumber) {
        return getQuantity(stickerNumber) >= 1;
    }

    public List<CollectionEntry> getAllEntries() {
        return entries.entrySet().stream()
                .map(e -> new CollectionEntry(e.getKey(), e.getValue()))
                .sorted((a, b) -> Integer.compare(a.getStickerNumber(), b.getStickerNumber()))
                .toList();
    }

    public List<CollectionEntry> getDuplicates() {
        List<CollectionEntry> duplicates = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : entries.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.add(new CollectionEntry(entry.getKey(), entry.getValue()));
            }
        }
        duplicates.sort((a, b) -> Integer.compare(a.getStickerNumber(), b.getStickerNumber()));
        return duplicates;
    }

    public List<Integer> getUniqueStickerNumbers() {
        return entries.keySet().stream()
                .filter(n -> getQuantity(n) >= 1)
                .sorted()
                .toList();
    }

    public List<Integer> getMissingStickerNumbers(int albumSize) {
        List<Integer> missing = new ArrayList<>();
        for (int i = 1; i <= albumSize; i++) {
            if (getQuantity(i) == 0) {
                missing.add(i);
            }
        }
        return missing;
    }
}
