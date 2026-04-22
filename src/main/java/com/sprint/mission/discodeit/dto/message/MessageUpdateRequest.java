package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID id,
        String title,
        String content
) {
}
