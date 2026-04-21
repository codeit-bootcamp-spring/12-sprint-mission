package com.sprint.mission.discodeit.dto.binary;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
