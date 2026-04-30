package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        String type, // "PUBLIC" or "PRIVATE"
        String name,
        String description,
        Instant lastMessageAt,
        List<UUID> participantIds
) {}
