package com.sprint.mission.discodeit.dto.status;

import java.util.UUID;

public record ReadStatuscreateRequest(
        UUID userId,
        UUID channelId
) {
}
