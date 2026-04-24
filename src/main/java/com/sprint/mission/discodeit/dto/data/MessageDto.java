package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record MessageDto (
    UUID id,
    UUID channelId,
    UUID senderId,
    String content
) {}


