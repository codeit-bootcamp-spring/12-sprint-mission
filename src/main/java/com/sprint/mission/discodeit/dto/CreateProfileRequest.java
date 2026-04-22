package com.sprint.mission.discodeit.dto;

public record CreateProfileRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
