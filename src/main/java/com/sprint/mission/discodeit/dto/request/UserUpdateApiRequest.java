package com.sprint.mission.discodeit.dto.request;

public record UserUpdateApiRequest(
        UserUpdateRequest user,
        BinaryContentCreateRequest profile
) {
}
