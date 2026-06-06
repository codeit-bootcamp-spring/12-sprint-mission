package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
    Instant updatedAt,
    String content,
    UUID channelId,
    UserResponse author,
    List<BinaryContentResponse> attachments
) {

}

