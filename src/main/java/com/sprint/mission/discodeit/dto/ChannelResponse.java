package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        UUID userId,
        String title,
        String description,
        ChannelType type,
        Instant updatedAt,
        List<UUID> memberIds
) {
}
