package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
	private UUID id;
	private String name;
	private List<User> users;
	private List<Message>  messages;
	private String link;
	private Long createdAt;
	private Long updatedAt;

	public Channel(String name, User creator) {
		id = UUID.randomUUID();
		this.name = name;
		this.users = new ArrayList<>();
		users.add(creator);
		messages = new ArrayList<>();
		link = "https://codeit.kr/channel/" + id;
		createdAt = System.currentTimeMillis();
		updatedAt = System.currentTimeMillis();
	}

	public void update(String name, List<User> users, List<Message> messages) {
		if (name != null) {
			this.name = name;
		}
		if (users != null) {
			this.users = users;
		}
		if (messages != null) {
			this.messages = messages;
		}
		updatedAt = System.currentTimeMillis();
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<Message> getMessages() {
		return messages;
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
		String updated =  Instant.ofEpochMilli(updatedAt).atZone(zonedId).toString();
		return """
        Channel {
            채널 ID= %s
            채널명= '%s'
            사용자= %s
            메시지= %s
            채널 링크= '%s'
            생성일= %s
            수정일= %s
        }
        """.formatted(
			id,
			name,
			users,
			messages,
			link,
			created,
			updated
		);
	}
}
