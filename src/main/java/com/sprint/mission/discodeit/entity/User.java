package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public class User {
	private final UUID id;
	private final Long createdAt;
	private String username;
	private String email;
	private String password;
	private String nickname;
	private String phoneNumber;
	private String icon;
	private Long updatedAt;

	public User(String username, String email, String password, String nickname, String phoneNumber, String icon) {
		id = UUID.randomUUID();
		this.username = username;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.icon = icon;
		createdAt = System.currentTimeMillis();
		updatedAt = System.currentTimeMillis();
	}

	public UUID getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getNickname() {
		return nickname;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public Long getUpdatedAt() {
		return updatedAt;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getIcon() {
		return icon;
	}

	public void update(String username, String email, String password, String nickname, String phoneNumber,
		String icon) {
		if (username != null) {
			this.username = username;
		}
		if (email != null) {
			this.email = email;
		}
		if (password != null) {
			this.password = password;
		}
		if (nickname != null) {
			this.nickname = nickname;
		}
		if (phoneNumber != null) {
			this.phoneNumber = phoneNumber;
		}
		if (icon != null) {
			this.icon = icon;
		}
		updatedAt = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		ZoneId zonedId = ZoneId.of("Asia/Seoul");
		String created = Instant.ofEpochMilli(createdAt).atZone(zonedId).toString();
		String updated = Instant.ofEpochMilli(updatedAt).atZone(zonedId).toString();
		return """ 
			User {
				사용자 ID: %s
				사용자명: %s
				이메일: %s
				비밀번호: %s
				별명: %s
				전화번호: %s
				이미지: %s
				생성일: %s
				수정일: %s
			}
			""".formatted(
			id,
			username,
			email,
			password,
			nickname,
			phoneNumber,
			icon,
			created,
			updated
		);
	}
}