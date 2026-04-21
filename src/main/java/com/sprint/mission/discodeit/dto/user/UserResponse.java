package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String nickname,
        UUID profileId,
        boolean isOnline
) {
}
