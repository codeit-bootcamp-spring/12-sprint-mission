package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.domain.channel.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant createdAt,
        Instant latestMessageAt,
        List<UUID> participantUserIds) {
}
