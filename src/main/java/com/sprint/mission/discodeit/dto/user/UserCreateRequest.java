package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;

public record UserCreateRequest(
        String username,
        String email,
        String password
) {
}
