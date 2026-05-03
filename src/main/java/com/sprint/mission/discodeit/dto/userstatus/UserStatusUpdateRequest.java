package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import lombok.NonNull;

import java.util.UUID;

public record UserStatusUpdateRequest(
    Instant newLastActiveAt
) {

}
