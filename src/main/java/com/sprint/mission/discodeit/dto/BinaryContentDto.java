package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        UUID userId,
        UUID messageId,
        byte[] binaryData,
        Instant createdAt
) {
    public static BinaryContentDto from(BinaryContent binaryContent) {
        return new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getUserId(),
                binaryContent.getMessageId(),
                binaryContent.getBinaryData(),
                binaryContent.getCreatedAt()
        );
    }
}
