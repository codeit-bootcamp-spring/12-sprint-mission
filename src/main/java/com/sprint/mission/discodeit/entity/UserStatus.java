package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final UUID userId;
	private final Instant createdAt;
	private Instant updatedAt;

	public UserStatus(UUID userId) {
		this.id = UUID.randomUUID();
		this.userId = userId;
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}
	public boolean lastStatus(){
		return Instant.now().isBefore(this.updatedAt.plus(5, ChronoUnit.MINUTES));
	}

}
