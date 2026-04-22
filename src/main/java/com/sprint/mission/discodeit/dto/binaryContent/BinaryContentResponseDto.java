package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentResponseDto(
        UUID id,
        byte[] imageContent,
        UUID userId,
        UUID authorId,
        UUID messageId
) {
    public static BinaryContentResponseDto from(BinaryContent entity) {
        return new BinaryContentResponseDto(
                entity.getId(),
                entity.getImageContent(),
                entity.getUserId(),
                entity.getAuthorId(),
                entity.getMessageId()
        );
    }
}