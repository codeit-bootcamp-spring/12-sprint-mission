package com.sprint.mission.discodeit.dto.User;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

// 요청
public record UserCreateRequest(
        String username,
        String email,
        String password,
        String nickname,
        BinaryContentCreateRequest profileImage
) {
    public User toUser(UUID profileImageId) {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImageId(profileImageId)
                .build();
    }
}