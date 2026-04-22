package com.sprint.mission.discodeit.data.request;

public record LoginRequest(
        String username,
        String password
) {
    public LoginRequest {
        if (username == null) {
            throw new IllegalArgumentException("username 없음");
        }

        if (password == null) {
            throw new IllegalArgumentException("password 없음");
        }
    }
}
