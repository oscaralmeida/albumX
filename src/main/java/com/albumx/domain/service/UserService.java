package com.albumx.domain.service;

import com.albumx.domain.exception.InvalidUserNameException;
import com.albumx.domain.model.User;
import com.albumx.domain.model.UserCollection;
import com.albumx.domain.repository.CollectionRepository;
import com.albumx.domain.repository.UserRepository;

import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;

    public UserService(UserRepository userRepository, CollectionRepository collectionRepository) {
        this.userRepository = userRepository;
        this.collectionRepository = collectionRepository;
    }

    public User register(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserNameException();
        }

        User user = new User(UUID.randomUUID(), name.trim());
        userRepository.save(user);
        collectionRepository.save(new UserCollection(user.getId()));
        return user;
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new com.albumx.domain.exception.UserNotFoundException(id));
    }
}
