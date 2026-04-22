package com.sprint.mission.discodeit.dto.User;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

// 응답
public record UserDto(
        UUID id,
        String username,
        String email,
        String nickname,
        boolean online,
        Instant createdAt,
        Instant updatedAt,
        UUID profileImageId
) {
    public static UserDto from(User user, UserStatus userStatus) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                userStatus != null && userStatus.isOnline(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfileImageId()
        );
    }
}