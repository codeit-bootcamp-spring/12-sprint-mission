package com.sprint.mission.discodeit.dto;

public record UpdateUserRequest(
        String username,
        String email,
        String password,
        CreateProfileRequest profile
) {
}
