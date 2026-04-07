package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReadStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final UUID userId;
	private final UUID changeId;
	private final Instant createdAt;
	private Instant updatedAt;

	public ReadStatus(UUID userId, UUID changeId) {
		this.id = UUID.randomUUID();
		this.userId = userId;
		this.changeId = changeId;
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}
}
