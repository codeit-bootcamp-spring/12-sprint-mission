package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID id,
    String name,
    String description,
    ChannelType type,
    Instant lastMessageAt,
    List<UUID> participantIds
) {

  public static ChannelDto from(Channel channel) {
    return new ChannelDto(
        channel.getId(),
        channel.getName(),
        channel.getDescription(),
        channel.getType(),
        channel.getUpdatedAt(),
        null
    );
  }
}
