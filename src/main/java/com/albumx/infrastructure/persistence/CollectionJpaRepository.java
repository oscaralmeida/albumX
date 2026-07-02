package com.albumx.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionJpaRepository extends JpaRepository<CollectionEntryEntity, Long> {

    List<CollectionEntryEntity> findByUserId(UUID userId);

    Optional<CollectionEntryEntity> findByUserIdAndStickerNumber(UUID userId, int stickerNumber);
}
