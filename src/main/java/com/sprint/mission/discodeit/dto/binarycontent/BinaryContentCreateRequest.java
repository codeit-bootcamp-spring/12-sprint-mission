package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.NonNull;
// 사용 안함
public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        Long fileSize,
        byte[] bytes
) {
}
