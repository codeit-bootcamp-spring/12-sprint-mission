package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record ChannelCreateRequest(
        String title,
        UUID userId,
        String description
) {
}
