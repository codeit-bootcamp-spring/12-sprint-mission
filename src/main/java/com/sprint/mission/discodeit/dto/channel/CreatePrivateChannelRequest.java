package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.domain.channel.ChannelType;
import com.sprint.mission.discodeit.domain.user.User;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChannelRequest(
        List<UUID> participantIds
) {
}
