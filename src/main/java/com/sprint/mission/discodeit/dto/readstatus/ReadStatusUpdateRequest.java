package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import lombok.NonNull;

import java.util.UUID;

public record ReadStatusUpdateRequest(
    Instant newLastReadAt
) {

}
