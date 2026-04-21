package com.sprint.mission.discodeit.dto.status;

import java.util.UUID;

public record UserStatusResponse(
        UUID id,
        UUID userId
) {
}
