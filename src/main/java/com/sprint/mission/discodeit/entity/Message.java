package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final UUID userId;
	private final UUID channelId;
	private String content;
	private final Long createdAt;
	private Long updatedAt;

	public Message(UUID userId, UUID channelId, String content) {
		id = UUID.randomUUID();
		this.userId = userId;
		this.content = content;
		this.channelId = channelId;
		createdAt = System.currentTimeMillis();
		updatedAt = System.currentTimeMillis();
	}

	public void update(String content) {
		this.content = content;
		updatedAt = System.currentTimeMillis();
	}

	public UUID getId() {
		return id;
	}

	public UUID getUserId() {
		return userId;
	}

	public UUID getChannelId() {
		return channelId;
	}

	public String getContent() {
		return content;
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

		return "Message{" +
			"id=" + id +
			", userId=" + userId +
			", channelId=" + channelId +
			", content='" + content + '\'' +
			", createdAt=" + created +
			", updatedAt=" + updated +
			'}';
	}
}
