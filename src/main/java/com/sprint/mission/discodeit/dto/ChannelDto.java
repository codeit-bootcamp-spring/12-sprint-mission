package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// 해당 채널의 가장 최근 메시지의 시간 정보를 포함해야함.
// private 채널인 경우 참여한 user의 id 정보를 포함


public record ChannelDto(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        ChannelType type,
        Instant lastMessageAt,
        List<UUID> memberIds
) {
    public static ChannelDto from(Channel channel, Instant lastMessageAt, List<UUID> memberIds) {
        return new ChannelDto(channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                lastMessageAt,
                memberIds);
    }
}
