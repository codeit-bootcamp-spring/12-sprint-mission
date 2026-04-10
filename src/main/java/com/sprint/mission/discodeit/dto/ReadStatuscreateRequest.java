package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ReadStatuscreateRequest(
        UUID userId,
        UUID channelId
) {
}
