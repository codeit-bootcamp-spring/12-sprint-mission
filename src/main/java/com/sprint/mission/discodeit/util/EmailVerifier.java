package com.sprint.mission.discodeit.util;

public class EmailVerifier {
    public static void isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("데이터가 비어 있음");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식.");
        }
    }
}
