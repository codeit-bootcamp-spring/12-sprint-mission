package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Message implements Serializable, Comparable<Message> {
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final Instant createdAt;
	private Instant updatedAt;
	//
	private String content;
	//
	private final UUID channelId;
	private final UUID authorId;
	private final List<UUID> attachmentIds;

	public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
		this.id = UUID.randomUUID();
		this.createdAt = Instant.now();
		//
		this.content = content;
		this.channelId = channelId;
		this.authorId = authorId;
		this.attachmentIds = attachmentIds;
	}

	public void update(String newContent) {
		boolean anyValueUpdated = false;
		if (newContent != null && !newContent.equals(this.content)) {
			this.content = newContent;
			anyValueUpdated = true;
		}

		if (anyValueUpdated) {
			this.updatedAt = Instant.now();
		}
	}

	@Override
	public int compareTo(Message o) {
		return this.getCreatedAt().compareTo(o.getCreatedAt());
	}
}
