package com.sprint.mission.discodeit.dto.data.channel;

import java.util.List;
import java.util.UUID;

public record ChannelCreatePrivateRequest(
        List<UUID> participantUserIds
) {
}