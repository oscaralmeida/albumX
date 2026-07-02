package com.albumx.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(@NotBlank String name) {
}
