package com.albumx.infrastructure.persistence;

import com.albumx.domain.model.UserCollection;
import com.albumx.domain.repository.CollectionRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CollectionRepositoryAdapter implements CollectionRepository {

    private final CollectionJpaRepository jpaRepository;

    public CollectionRepositoryAdapter(CollectionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserCollection save(UserCollection collection) {
        UUID userId = collection.getUserId();
        Map<Integer, Integer> currentEntries = collection.getEntries();
        List<CollectionEntryEntity> existingEntities = jpaRepository.findByUserId(userId);

        for (CollectionEntryEntity entity : existingEntities) {
            Integer qty = currentEntries.get(entity.getStickerNumber());
            if (qty == null || qty == 0) {
                jpaRepository.delete(entity);
            } else if (!qty.equals(entity.getQuantity())) {
                entity.setQuantity(qty);
                jpaRepository.save(entity);
            }
        }

        for (var entry : collection.getAllEntries()) {
            boolean exists = existingEntities.stream()
                    .anyMatch(e -> e.getStickerNumber() == entry.getStickerNumber());
            if (!exists) {
                jpaRepository.save(new CollectionEntryEntity(
                        userId, entry.getStickerNumber(), entry.getQuantity()));
            }
        }
        return collection;
    }

    @Override
    public Optional<UserCollection> findByUserId(UUID userId) {
        List<CollectionEntryEntity> entities = jpaRepository.findByUserId(userId);
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        Map<Integer, Integer> entries = new HashMap<>();
        for (CollectionEntryEntity entity : entities) {
            entries.put(entity.getStickerNumber(), entity.getQuantity());
        }
        return Optional.of(new UserCollection(userId, entries));
    }
}
