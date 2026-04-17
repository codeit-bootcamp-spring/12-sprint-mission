package com.sprint.mission.discodeit.dto;

public record CreateAttachmentRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
