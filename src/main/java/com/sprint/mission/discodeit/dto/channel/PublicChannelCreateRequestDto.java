package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

public record PublicChannelCreateRequestDto(
        ChannelType type,
        String name,
        String description
) {
}
