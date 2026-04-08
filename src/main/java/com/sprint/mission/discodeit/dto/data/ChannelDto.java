package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelDto(
	UUID id,
	ChannelType channelType,
	String name,
	String description,
	List<UUID> userIds,
	Instant lastMessageAt
) {
	public static ChannelDto from(Channel channel, Instant lastMessageAt, List<UUID> userIds) {
		return new ChannelDto(
			channel.getId(),
			channel.getType(),
			channel.getName(),
			channel.getDescription(),
			userIds,
			lastMessageAt
		);
	}
}
