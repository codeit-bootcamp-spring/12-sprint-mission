package com.sprint.mission.discodeit.dto.auth;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(
        UUID id,
        Instant loginTime
) {
}
