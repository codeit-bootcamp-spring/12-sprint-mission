package com.sprint.mission.discodeit.dto.Channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

public record PublicChannelCreateRequest(
        String name,
        String description
) {
    public Channel toChannel() {
        return Channel.builder()
                .name(name)
                .description(description)
                .type(ChannelType.PUBLIC)
                .build();
    }
}