package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelUpdateRequestDto(
        UUID channelId,
        ChannelType type,
        String name,
        String description
) {
}
