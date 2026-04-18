package com.sprint.mission.discodeit.dto.request;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class UserCreateRequest {
    private final String username;
    private final String email;
    private final String password;

    @Nullable
    private final BinaryContentCreateRequest profileImage;
}
