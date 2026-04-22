package com.sprint.mission.discodeit.dto.BinaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentCreateRequest(
        UUID userId,
        UUID messageId,
        byte[] binaryData
) {
    public BinaryContent toBinaryContent() {
        return BinaryContent.builder()
                .userId(userId)
                .messageId(messageId)
                .binaryData(binaryData)
                .build();
    }
}
