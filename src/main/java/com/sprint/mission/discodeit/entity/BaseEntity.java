package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class BaseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final Instant createdAt;
	private Instant updatedAt;

	protected BaseEntity() {
		this.id = UUID.randomUUID();
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}

	protected void updateAtUpdate(){
		this.updatedAt = Instant.now();
	}

}
