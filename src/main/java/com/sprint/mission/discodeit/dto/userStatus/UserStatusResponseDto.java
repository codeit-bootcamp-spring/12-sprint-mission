package com.sprint.mission.discodeit.dto.userStatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserStatusResponseDto(
        UUID id,
        UUID userId,
        boolean isOnline,
        LocalDateTime lastConnectedAt,
        Instant updatedAt
) {
    public static UserStatusResponseDto from(UserStatus status) {
        return new UserStatusResponseDto(
                status.getId(),
                status.getUserId(),
                status.isOnline(),
                status.getLastConnectedAt(),
                status.getUpdatedAt()
        );
    }
}