package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter
public class UserStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MINUTES = 5;
	private final UUID id;
	private final Instant createdAt;
	private Instant updatedAt;
	//
	private final UUID userId;
	private Instant lastActiveAt;

	public UserStatus(UUID userId, Instant lastActiveAt) {
		this.id = UUID.randomUUID();
		this.createdAt = Instant.now();
		//
		this.userId = userId;
		this.lastActiveAt = lastActiveAt;
	}

	public void update(Instant lastActiveAt) {
		boolean anyValueUpdated = false;
		if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
			this.lastActiveAt = lastActiveAt;
			anyValueUpdated = true;
		}

		if (anyValueUpdated) {
			this.updatedAt = Instant.now();
		}
	}

	public Boolean isOnline() {
		Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(MINUTES));

		return lastActiveAt.isAfter(instantFiveMinutesAgo);
	}
}
