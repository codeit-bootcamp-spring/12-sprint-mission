package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID channelId,
        UUID userId,
        String title,
        String content,
        List<UUID> attachmentIds
) {
}
