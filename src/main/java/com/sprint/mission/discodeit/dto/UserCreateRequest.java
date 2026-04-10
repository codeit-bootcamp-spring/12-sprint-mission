package com.sprint.mission.discodeit.dto;

public record UserCreateRequest(
        String name,
        String email,
        String nickname,
        String password,
        String profileFileName,
        String profileUrl
) {
}
