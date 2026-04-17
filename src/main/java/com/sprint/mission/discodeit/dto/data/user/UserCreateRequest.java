package com.sprint.mission.discodeit.dto.data.user;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        byte[] profileImageBytes,
        String profileImageName,
        String profileImageContentType
) {
}