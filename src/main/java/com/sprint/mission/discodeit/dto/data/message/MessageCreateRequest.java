package com.sprint.mission.discodeit.dto.data.message;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<AttachmentData> attachments
) {

    public record AttachmentData(
            byte[] bytes,
            String fileName,
            String contentType
    ) {}
}