package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageUpdateRequestDto(
        UUID messageId,
        UUID channelId,
        UUID authorId,
        String content,
        List<byte[]> files

) {
}
