package com.sprint.mission.discodeit.dto.data.readstatus;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId
) {
}