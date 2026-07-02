package com.albumx.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddStickerRequest(@NotNull @Min(1) Integer stickerNumber) {
}
