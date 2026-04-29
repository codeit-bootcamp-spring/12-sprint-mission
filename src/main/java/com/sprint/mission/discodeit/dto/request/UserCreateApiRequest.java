package com.sprint.mission.discodeit.dto.request;

public record UserCreateApiRequest(
        UserCreateRequest user,
        BinaryContentCreateRequest profile
) {
}
