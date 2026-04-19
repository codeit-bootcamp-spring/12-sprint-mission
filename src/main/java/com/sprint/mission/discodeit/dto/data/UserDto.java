package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserDto (
        UUID id,
        String userName,
//        String password,
        String email,
        String nickName,
        UUID profileId,

        Instant createdAt,
        Instant updatedAt
) {
    public static UserDto from(User user, UserStatus userStatus) {
        return new UserDto(user.getId(),
                user.getUserName(),
//                user.getPassword(), // 추후 보안 보완 필요
                user.getEmail(),
                user.getNickName(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
