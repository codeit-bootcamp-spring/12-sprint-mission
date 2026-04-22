package com.sprint.mission.discodeit.dto.binaryContent;

import java.util.UUID;

public record MessageImageCreateRequestDto(
        UUID authorId,
        UUID messageId,
        byte[] imageContent
) {

}
