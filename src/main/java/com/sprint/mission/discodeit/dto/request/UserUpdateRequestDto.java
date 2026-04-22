package com.sprint.mission.discodeit.dto.request;

public record UserUpdateRequestDto(
	String newUsername,
	String newEmail,
	String newPassword,
	byte[] newProfileImage
) {
}
