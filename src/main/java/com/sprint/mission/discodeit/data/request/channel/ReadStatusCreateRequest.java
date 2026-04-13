package com.sprint.mission.discodeit.data.request.channel;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId
) {
    public ReadStatusCreateRequest {
        if (userId == null) {
            throw new IllegalArgumentException("userId 없음");
        }
        if (channelId == null) {
            throw new IllegalArgumentException("channelId 없음");
        }
    }
}
