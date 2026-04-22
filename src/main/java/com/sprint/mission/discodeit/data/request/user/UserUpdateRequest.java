package com.sprint.mission.discodeit.data.request.user;

import java.util.Optional;
import java.util.UUID;
import com.sprint.mission.discodeit.data.request.message.BinaryContentCreateRequest;

public record UserUpdateRequest(
        String username,
        String password,
        String email,
        UUID profileId,
        Optional<BinaryContentCreateRequest> binaryContentCreateRequest
) {
    public UserUpdateRequest {
        if (username == null) {
            throw new IllegalArgumentException("username 없음");
        }

        if (password == null) {
            throw new IllegalArgumentException("password 없음");
        }

        if (email == null) {
            throw new IllegalArgumentException("email 없음");
        }
    }
}
