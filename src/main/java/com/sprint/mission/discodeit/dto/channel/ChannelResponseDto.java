package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDto(
        UUID id,
        String name,
        String description,
        ChannelType channelType,
        Instant lastMessageAt,
        List<UUID> userIds
) {
    public static ChannelResponseDto from(Channel channel, List<UUID> userIds, Instant lastMessageAt) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getType(),
                lastMessageAt,
                userIds
        );
    }


    public static ChannelResponseDto publicChannelFrom(Channel channel, Instant lastMessageAt) {
        return from(channel, null, lastMessageAt);
    }


    public static ChannelResponseDto privateChannelFrom(Channel channel, List<UUID> userIds, Instant lastMessageAt) {
       return from(channel, userIds, lastMessageAt);
    }

}