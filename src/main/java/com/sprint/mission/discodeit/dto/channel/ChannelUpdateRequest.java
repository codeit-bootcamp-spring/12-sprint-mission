package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID id,
        String title,
        String description
) {
}
