package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelUpdateDto(
        UUID id,
        String channelName,
        String channelDescription,
        ChannelType type,
        boolean isPrivate
)
{}
