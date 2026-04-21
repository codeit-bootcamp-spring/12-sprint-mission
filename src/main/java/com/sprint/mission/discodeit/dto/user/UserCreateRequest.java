package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;

import java.util.UUID;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        BinaryContentCreateRequest profileImage
) {}
