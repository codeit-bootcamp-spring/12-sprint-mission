package com.sprint.mission.discodeit.data.request.channel;

import com.sprint.mission.discodeit.entity.channel.ChannelType;

public record PublicChannelCreateRequest(
        ChannelType type,
        String name,
        String description
) {
    public PublicChannelCreateRequest {
        if (type == null) {
            type = ChannelType.TEXT;
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름이 없음");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("설명이 없음");
        }
    }
}