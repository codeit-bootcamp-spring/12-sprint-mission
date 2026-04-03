package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private ChannelType type;
	private String name;
	private String description;
	private UUID creator;
	private final Long createdAt;
	private Long updatedAt;

	public Channel(ChannelType type, String name, String description, UUID creator) {
		id = UUID.randomUUID();
		this.type = type;
		this.name = name;
		this.description = description;
		this.creator = creator;
		createdAt = System.currentTimeMillis();
		updatedAt = System.currentTimeMillis();
	}

	public void update(String name, String description) {
		if (name != null) {
			this.name = name;
		}
		if (description != null) {
			this.description = description;
		}
		updatedAt = System.currentTimeMillis();
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ChannelType getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public UUID getCreator() {
		return creator;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public Long getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public String toString() {
		ZoneId zonedId = ZoneId.of("Asia/Seoul");
		String created = Instant.ofEpochMilli(createdAt).atZone(zonedId).toString();
		String updated = Instant.ofEpochMilli(updatedAt).atZone(zonedId).toString();

		return "Channel{" +
			"id=" + id +
			", type=" + type +
			", name='" + name + '\'' +
			", description='" + description + '\'' +
			", creator=" + creator +
			", createdAt=" + created +
			", updatedAt=" + updated +
			'}';
	}
}
