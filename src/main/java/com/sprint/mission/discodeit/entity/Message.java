package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class Message extends BaseEntity implements Comparable<Message> {
	private final UUID channelId;
	private final UUID authorId;
	private String content;
	private final List<UUID> attachmentIds;

	@Builder
	public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
		super();
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
			updateAtUpdate();
		}
	}

	@Override
	public int compareTo(Message o) {
		return this.getCreatedAt().compareTo(o.getCreatedAt());
	}

}
