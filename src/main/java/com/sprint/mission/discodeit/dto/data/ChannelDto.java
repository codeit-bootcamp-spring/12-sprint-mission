package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String name,
        ChannelType type,
        boolean isPrivate
) {
    public ChannelDto from(Channel channel) {
        return new ChannelDto(channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getName(),
                channel.getType(),
                channel.isPrivate());
    }
}
