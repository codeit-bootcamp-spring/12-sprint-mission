package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
	private UUID id;
	private UUID userUUID;
	private String username;
	private String content;
	private String link;
	private Long createdAt;
	private Long updatedAt;

	public Message(UUID userUUID, String username, String content) {
		id = UUID.randomUUID();
		this.userUUID = userUUID;
		this.username = username;
		this.content = content;
		link = "https://codeit.kr/" + UUID.randomUUID();
		createdAt = System.currentTimeMillis();
		updatedAt = System.currentTimeMillis();
	}

	public void update(String content){
		this.content = content;
		updatedAt = System.currentTimeMillis();
	}

	public UUID getId() {
		return id;
	}

	public UUID getUserUUID() {
		return userUUID;
	}

	public String getUsername() {
		return username;
	}

	public String getContent() {
		return content;
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
		return """
        Message {
            메시지 ID= %s
            사용자 ID= %s
            사용자명= '%s'
            내용= '%s'
            메시지 링크= '%s'
            생성일= %s
            수정일= %s
        }
        """.formatted(
			id,
			userUUID,
			username,
			content,
			link,
			createdAt,
			updatedAt
		);
	}
}
