package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
        UUID id,
        UUID userId,
        Instant createdAt,
        Instant updatedAt,
        Instant lastSeenAt
) {
    public static UserStatusDto from(UserStatus userStatus) {
        return new UserStatusDto(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getLastSeenAt()
        );
    }
}