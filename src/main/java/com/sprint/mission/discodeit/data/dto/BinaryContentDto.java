package com.sprint.mission.discodeit.data.dto;

import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        String fileName,
        String fileData
) {
}
