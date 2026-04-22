package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.UUID;

public record UserCreateRequest(
        String userName,
        String password,
        String email,
        String nickName,
        UUID profileId
) {
    public User toUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .userName(userName)
                .password(password)
                .email(email)
                .nickName(nickName)
                .profileId(profileId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
