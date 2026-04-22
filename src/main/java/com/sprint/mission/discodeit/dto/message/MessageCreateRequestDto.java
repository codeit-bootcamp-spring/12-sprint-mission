package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequestDto(
        String content,
        UUID channelId,
        UUID authorId,
        List<byte[]> files

) {
}
