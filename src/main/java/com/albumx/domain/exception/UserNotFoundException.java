package com.albumx.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(UUID userId) {
        super("USER_NOT_FOUND", "Usuário não encontrado: " + userId);
    }
}
