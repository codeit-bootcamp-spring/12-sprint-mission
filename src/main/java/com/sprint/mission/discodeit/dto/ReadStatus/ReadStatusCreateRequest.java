package com.sprint.mission.discodeit.dto.ReadStatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId
) {
    public ReadStatus toReadStatus() {
        return ReadStatus.builder()
                .userId(userId)
                .channelId(channelId)
                .build();
    }
}
