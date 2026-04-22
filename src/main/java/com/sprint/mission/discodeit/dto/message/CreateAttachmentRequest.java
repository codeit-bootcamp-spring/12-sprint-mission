package com.sprint.mission.discodeit.dto.message;

public record CreateAttachmentRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
