package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        String fileName,
        String url
) {
}
