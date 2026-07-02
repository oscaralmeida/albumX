package com.albumx.application.dto;

import java.util.List;
import java.util.UUID;

public record CollectionResponse(UUID userId, List<CollectionEntryResponse> entries) {
}
