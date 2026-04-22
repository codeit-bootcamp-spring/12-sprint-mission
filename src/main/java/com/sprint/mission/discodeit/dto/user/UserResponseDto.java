package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        boolean isOnline,
        UUID profileImageId
) {

    public static UserResponseDto from(User user, UserStatus status, UUID profileImageId) {
        return new UserResponseDto(
                user.getId()
                ,user.getUsername()
                ,user.getEmail()
                ,status.isOnline()
                ,profileImageId
        );
    }
}
