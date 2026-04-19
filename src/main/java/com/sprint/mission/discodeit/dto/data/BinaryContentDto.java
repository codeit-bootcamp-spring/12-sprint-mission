package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        Instant createdAt,
        String filePath
) {
    public BinaryContentDto from(BinaryContent binaryContent) {
        return new BinaryContentDto(binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFilePath());
    }
}
