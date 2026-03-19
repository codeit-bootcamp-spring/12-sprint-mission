package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
	private UUID id;
	private String username;
	private String email;
	private String password;
	private String nickname;
	private String phoneNumber;
	private String icon;
	private Long createdAt;
	private Long updatedAt;

	public User(String username, String email, String password, String nickname,  String phoneNumber, String icon) {
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

	public void update(String username, String email, String password, String nickname,  String phoneNumber,  String icon) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.icon = icon;
		updatedAt = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "User{" +
			"사용자 ID=" + id +
			", 사용자명='" + username + '\'' +
			", 이메일='" + email + '\'' +
			", 비밀번호='" + password + '\'' +
			", 별명='" + nickname + '\'' +
			", 휴대폰='" + phoneNumber + '\'' +
			", 이미지='" + icon + '\'' +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			'}';
	}
}