package com.sprint.mission.discodeit.data.request.message;

public record BinaryContentCreateRequest(
        String fileName,
        String fileData
) {
    public BinaryContentCreateRequest {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName 없음");
        }

        if (fileData == null) {
            throw new IllegalArgumentException("fileData 없음");
        }
    }
}