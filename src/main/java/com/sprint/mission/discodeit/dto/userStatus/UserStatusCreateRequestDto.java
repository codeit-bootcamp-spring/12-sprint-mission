package com.sprint.mission.discodeit.dto.userStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserStatusCreateRequestDto(
        UUID userId
) {
}
