package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id,
        UUID channelId,
        UUID memberId,
        Instant createdAt,
        Instant updatedAt,
        String content,
        List<UUID>attachmentIds,
        boolean isEdited
) {
    public static MessageDto from(Message message) {
        return new MessageDto(message.getId(),
                message.getChannelId(),
                message.getMemberId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                List.copyOf(message.getAttachmentIds()), // 불변성 유지
                message.isEdited());
    }
}
