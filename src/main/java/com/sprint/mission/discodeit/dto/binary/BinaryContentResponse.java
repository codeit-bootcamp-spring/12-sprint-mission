package com.sprint.mission.discodeit.dto.binary;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        String fileName,
        String contentType,
        long size,
        Instant createdAt
) {}
