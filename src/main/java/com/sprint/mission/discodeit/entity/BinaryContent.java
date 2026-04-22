package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BinaryContent implements Serializable, Comparable<BinaryContent> {
	@Serial
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final byte[] content;
	private final Instant createdAt;

	@Builder
	public BinaryContent(byte[] content) {
		this.id = UUID.randomUUID();
		this.content = content;
		this.createdAt = Instant.now();
	}

	@Override
	public int compareTo(BinaryContent o) {
		return this.createdAt.compareTo(o.getCreatedAt());
	}
}
