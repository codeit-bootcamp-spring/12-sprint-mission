package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserStatusResponse(
        UUID id,
        UUID userId
) {
}
