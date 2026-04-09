package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
        UUID id,
        String content,
        UUID userId,
        UUID channelId,
        Instant createdAt,
        Instant updatedAt
) {
    public static MessageDto from(Message message) {
        return new MessageDto(
                message.getId(),
                message.getContent(),
                message.getUserId(),
                message.getChannelId(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}