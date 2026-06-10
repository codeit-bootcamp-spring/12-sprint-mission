package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UserResponse> participants,
    Instant lastMessageAt
) {

  public static ChannelResponse from(Channel channel, List<UserResponse> participants,
      Instant lastMessageAt) {
    return new ChannelResponse(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt
    );
  }
}
