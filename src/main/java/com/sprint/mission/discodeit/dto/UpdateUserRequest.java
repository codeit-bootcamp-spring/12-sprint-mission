package com.sprint.mission.discodeit.dto;

public record UpdateUserRequest(
        String newUsername,
        String newEmail,
        String newPassword,
        String contentType,
        byte[] data
) {
}
