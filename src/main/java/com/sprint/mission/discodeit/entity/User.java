package com.sprint.mission.discodeit.entity;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class User extends BaseEntity implements Comparable<User> {
	private UUID profileId;
	private String username;
	private String email;
	private String password;

	@Builder
	public User(String username, String email, String password, UUID profileId) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.profileId = profileId;
	}

	public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
		boolean anyValueUpdated = false;
		if (newUsername != null && !newUsername.equals(this.username)) {
			this.username = newUsername;
			anyValueUpdated = true;
		}
		if (newEmail != null && !newEmail.equals(this.email)) {
			this.email = newEmail;
			anyValueUpdated = true;
		}
		if (newPassword != null && !newPassword.equals(this.password)) {
			this.password = newPassword;
			anyValueUpdated = true;
		}

		if (newProfileId != null && !newProfileId.equals(this.profileId)) {
			this.profileId = newProfileId;
			anyValueUpdated = true;
		}

		if (anyValueUpdated) {
			updateAtUpdate();
		}
	}

	@Override
	public int compareTo(User o) {
		return this.getCreatedAt().compareTo(o.getCreatedAt());
	}
}
