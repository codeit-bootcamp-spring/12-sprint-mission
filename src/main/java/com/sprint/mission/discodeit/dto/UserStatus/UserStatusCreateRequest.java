package com.sprint.mission.discodeit.dto.UserStatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserStatusCreateRequest(
        UUID userId
) {
    public UserStatus toUserStatus() {
        return UserStatus.builder()
                .userId(userId)
                .build();
    }
}
