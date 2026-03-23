package com.sprint.mission.discodeit.DTO;

public record CreateUserRequest(String username, String password, String email, String nickname) {
    public CreateUserRequest {
        validate(username, "username");
        validate(password, "password");
        validate(email, "email");
        validate(nickname, "nickname");

        validateEmailFormat(email);
    }

    private void validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " 데이터가 비어 있음");
        }
    }

    private void validateEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식 존재.");
        }
    }
}