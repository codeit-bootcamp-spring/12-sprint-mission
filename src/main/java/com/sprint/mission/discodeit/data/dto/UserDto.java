package com.sprint.mission.discodeit.data.dto;

import java.util.UUID;
import com.sprint.mission.discodeit.entity.user.User;

public record UserDto(
        UUID id,
        String username,
        String email,
        UUID profileId,
        boolean online
) {
}
