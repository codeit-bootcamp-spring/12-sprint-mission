package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.user.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        BinaryContentResponse profile,
        Boolean online,
        Role role
) {
}