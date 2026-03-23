package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record CreateChannelRequest(UUID channelOwnerId, ChannelType type, String name, boolean isPrivate) {
    public CreateChannelRequest {
        if (channelOwnerId == null) {
            throw new IllegalArgumentException("채널 주인이 없음");
        }

        if (type == null) {
            type = ChannelType.TEXT;
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름이 없음");
        }
    }
}