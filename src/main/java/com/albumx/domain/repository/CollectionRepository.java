package com.albumx.domain.repository;

import com.albumx.domain.model.UserCollection;

import java.util.Optional;
import java.util.UUID;

public interface CollectionRepository {

    UserCollection save(UserCollection collection);

    Optional<UserCollection> findByUserId(UUID userId);
}
