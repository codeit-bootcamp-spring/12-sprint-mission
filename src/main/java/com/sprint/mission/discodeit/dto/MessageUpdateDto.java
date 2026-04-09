package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record MessageUpdateDto(
        UUID id,
        UUID roomId,
        UUID senderId,
        String content
) {
}
