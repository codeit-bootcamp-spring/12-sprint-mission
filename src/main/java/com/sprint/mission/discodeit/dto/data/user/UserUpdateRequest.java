package com.sprint.mission.discodeit.dto.data.user;

public record UserUpdateRequest(
        String username,
        String email,
        String password,
        byte[] profileImageBytes,
        String profileImageName,
        String profileImageContentType
) {
}