package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateRequest(
        UUID id,
        UUID profileId,
        String userName,
        String password,
        String email,
        String nickName
) {
}
