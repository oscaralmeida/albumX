package com.albumx.domain.exception;

public class InvalidUserNameException extends DomainException {

    public InvalidUserNameException() {
        super("INVALID_USER_NAME", "O nome do usuário não pode ser vazio.");
    }
}
