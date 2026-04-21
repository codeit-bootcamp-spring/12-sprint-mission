package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        String newUsername,
        String newEmail,
        String newPassword,
        BinaryContentCreateRequest newProfileImage
) {}
