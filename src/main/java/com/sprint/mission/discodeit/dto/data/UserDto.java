package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public record UserDto(
	UUID id,
	String username,
	String email,
	UUID profileId,
	boolean status,
	Instant createdAt,
	Instant updatedAt) {
	public static UserDto from(User user, boolean status) {
		return new UserDto(user.getId(),
			user.getUsername(),
			user.getEmail(),
			user.getProfileId(),
			status,
			user.getCreatedAt(),
			user.getUpdatedAt());
	}
}
