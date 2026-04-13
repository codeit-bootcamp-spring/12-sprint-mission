package com.sprint.mission.discodeit.data.dto;

import com.sprint.mission.discodeit.entity.channel.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        ChannelType type,
        String name,
        List<UUID> userIdList,
        Instant latestMessage
) {
}