package com.sprint.mission.discodeit.dto.data.binarycontent;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record BinaryContentResponse(
        UUID id,
        byte[] bytes,
        String fileName,
        String contentType,
        Instant createdAt
) {
}