package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Base64;

public record BinaryContentDto(
        String id,
        String contentType,
        String bytes
) {
    public static BinaryContentDto from(BinaryContent entity) {
        return new BinaryContentDto(
                entity.getId().toString(),
                entity.getContentType(),
                Base64.getEncoder().encodeToString(entity.getBytes())
        );
    }
}