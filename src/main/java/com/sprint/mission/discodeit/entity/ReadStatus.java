package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class ReadStatus extends BaseEntity implements Comparable<ReadStatus> {
	private final UUID userId;
	private final UUID channelId;
	private Instant lastReadAt;

	@Builder
	public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
		super();
		this.userId = userId;
		this.channelId = channelId;
		this.lastReadAt = lastReadAt;
	}

	public void update(Instant lastReadAt) {
		this.lastReadAt = lastReadAt;
		updateAtUpdate();
	}

	@Override
	public int compareTo(ReadStatus o) {
		return this.getCreatedAt().compareTo(o.getCreatedAt());
	}
}
