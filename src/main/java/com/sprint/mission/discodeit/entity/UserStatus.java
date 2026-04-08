package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserStatus implements Serializable, Comparable<UserStatus> {
	@Serial
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final UUID userId;
	private final Instant createdAt;
	private Instant updatedAt;
	private Instant lastOnlineTime;

	@Builder
	public UserStatus(UUID userId, Instant lastLogin) {
		this.id = UUID.randomUUID();
		this.userId = userId;
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
		this.lastOnlineTime = lastLogin;
	}

	public void update(Instant lastLogin){
		 this.lastOnlineTime = lastLogin;
	}

	public boolean isOnline(){
		return Instant.now().isBefore(this.lastOnlineTime.plus(5, ChronoUnit.MINUTES));
	}

	@Override
	public int compareTo(UserStatus o) {
		return this.createdAt.compareTo(o.getCreatedAt());
	}
}
