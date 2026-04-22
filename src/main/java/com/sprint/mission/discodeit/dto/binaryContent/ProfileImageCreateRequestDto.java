package com.sprint.mission.discodeit.dto.binaryContent;

import java.util.UUID;

public record ProfileImageCreateRequestDto(
        UUID userId,
        byte[] imageContent
) {
}
