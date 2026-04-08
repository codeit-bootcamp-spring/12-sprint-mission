package com.sprint.mission.discodeit.dto;

public record UserDto(
        Long id,
        String username,
        String email,
        boolean online
) {
}
