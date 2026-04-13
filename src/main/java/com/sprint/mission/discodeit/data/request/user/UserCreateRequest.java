package com.sprint.mission.discodeit.data.request.user;

import java.util.Optional;
import com.sprint.mission.discodeit.data.request.message.BinaryContentCreateRequest;

public record UserCreateRequest(
        String username,
        String password,
        String email,
        Optional<BinaryContentCreateRequest> binaryContentCreateRequest
) {
    public UserCreateRequest {
        if (username == null) {
            throw new IllegalArgumentException("username 없음");
        }

        if (password == null) {
            throw new IllegalArgumentException("password 없음");
        }

        if (email == null) {
            throw new IllegalArgumentException("email 없음");
        }

        validateEmailFormat(email);
    }

    private void validateEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식 존재.");
        }
    }
}