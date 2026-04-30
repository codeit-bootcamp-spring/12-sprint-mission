package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.UUID;

// 보통 응답으로 사용될 DTO
public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        Boolean online
) {
}
