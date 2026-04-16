package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class UserStatus extends BaseEntity implements Comparable<UserStatus> {
	private final UUID userId;
	private Instant lastOnlineTime;

	@Builder
	public UserStatus(UUID userId, Instant lastLogin) {
		super();
		this.userId = userId;
		this.lastOnlineTime = lastLogin;
	}

	public void update(Instant lastLogin) {
		this.lastOnlineTime = lastLogin;
		updateAtUpdate();
	}

	public boolean isOnline() {
		return Instant.now().isBefore(this.lastOnlineTime.plus(5, ChronoUnit.MINUTES));
	}

	@Override
	public int compareTo(UserStatus o) {
		return this.getCreatedAt().compareTo(o.getCreatedAt());
	}
}
