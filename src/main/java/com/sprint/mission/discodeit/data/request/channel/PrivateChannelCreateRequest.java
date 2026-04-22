package com.sprint.mission.discodeit.data.request.channel;

import java.util.List;
import java.util.UUID;
import com.sprint.mission.discodeit.entity.channel.ChannelType;

public record PrivateChannelCreateRequest(
        ChannelType type,
        List<UUID> userIdList
) {
    public PrivateChannelCreateRequest {
        if (type == null) {
            type = ChannelType.TEXT;
        }

        if (userIdList == null || userIdList.isEmpty()) {
            throw new IllegalArgumentException("유저 없음");
        }
    }
}