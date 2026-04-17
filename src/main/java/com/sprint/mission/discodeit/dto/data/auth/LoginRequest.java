package com.sprint.mission.discodeit.dto.data.auth;

public record LoginRequest(
        String username,
        String password
) {
}