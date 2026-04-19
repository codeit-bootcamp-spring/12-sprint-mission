package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant createdAt,
        Instant updatedAt
) {
    public ReadStatusDto from(ReadStatus readStatus){
        return new ReadStatusDto(readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt());
    }
}
