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
	private final Long createdAt;
	private String content;
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
		return """
			Message {
			    메시지 ID: %s
			    사용자 ID: %s
			    채널	ID: %s
			    내용: '%s'
			    생성일: %s
			    수정일: %s
			}
			""".formatted(
			id,
			userId,
			channelId,
			content,
			created,
			updated
		);
	}
}
