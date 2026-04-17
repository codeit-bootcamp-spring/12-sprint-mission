package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID channelId,
        UUID userId,
        String title,
        String content,
        List<UUID> attachmentIds
) {
}
