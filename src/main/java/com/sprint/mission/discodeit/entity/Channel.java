package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final String link;
	private final Long createdAt;
	private ChannelType type;
	private String name;
	private String description;
	private UUID creator;
	private Long updatedAt;

	public Channel(ChannelType type, String name, String description, UUID creator) {
		id = UUID.randomUUID();
		this.type = type;
		this.name = name;
		this.description = description;
		this.creator = creator;
		link = "https://codeit.kr/channel/" + UUID.randomUUID();
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

	public String getLink() {
		return link;
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
		return """
			Channel {
			    채널 ID: %s
			    채널 타입: %s
			    채널명: '%s'
			    관리자: %s
			    채널 링크: '%s'
			    생성일: %s
			    수정일: %s
			}
			""".formatted(
			id,
			type,
			name,
			creator,
			link,
			created,
			updated
		);
	}
}
