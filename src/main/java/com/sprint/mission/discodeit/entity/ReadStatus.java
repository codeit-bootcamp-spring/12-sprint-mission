package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReadStatus implements Serializable, Comparable<ReadStatus> {
	@Serial
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final UUID userId;
	private final UUID channelId;
	private final Instant createdAt;
	private Instant updatedAt;
	private Instant lastReadAt;

	@Builder
	public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
		this.id = UUID.randomUUID();
		this.userId = userId;
		this.channelId = channelId;
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
		this.lastReadAt = lastReadAt;
	}

	public void update(Instant lastReadAt) {
		this.lastReadAt = lastReadAt;
		this.updatedAt = Instant.now();
	}

	@Override
	public int compareTo(ReadStatus o) {
		return this.createdAt.compareTo(o.getCreatedAt());
	}
}
