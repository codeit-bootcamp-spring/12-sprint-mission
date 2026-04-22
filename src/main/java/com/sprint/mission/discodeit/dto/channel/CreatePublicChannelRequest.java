package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.domain.channel.ChannelType;

public record CreatePublicChannelRequest(
        String name,
        String description) {
}
