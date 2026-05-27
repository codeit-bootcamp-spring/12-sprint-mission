package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;

public record ErrorResponse(
    int code,
    String message,
    Instant timestamp
) {

}
