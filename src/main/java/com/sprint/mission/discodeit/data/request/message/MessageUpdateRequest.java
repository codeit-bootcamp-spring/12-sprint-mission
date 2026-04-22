package com.sprint.mission.discodeit.data.request.message;

import java.util.List;
import java.util.UUID;

public record MessageUpdateRequest(
        String content,
        List<UUID> attachmentIds
) {
}
