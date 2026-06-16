package com.sprint.mission.discodeit.dto.data.binarycontent;

public record BinaryContentCreateRequest(
        byte[] bytes,
        String fileName,
        String contentType
) {
}