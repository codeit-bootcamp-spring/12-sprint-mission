package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateRequest(
        UUID id,
        String nickname,
        UUID profileId,
        String password
) {
}
