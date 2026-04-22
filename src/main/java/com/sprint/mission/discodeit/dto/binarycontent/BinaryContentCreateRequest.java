package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentCreateRequest (
        byte[] data,
        String filename,
        String mimeType
) {
}
