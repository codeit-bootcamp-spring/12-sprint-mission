package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 2, max = 30, message = "사용자 이름은 2자 이상 30자 이하여야 합니다.")
        String newUsername,

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String newEmail,

        @Size(min = 4, max = 50, message = "비밀번호는 4자 이상 50자 이하여야 합니다.")
        String newPassword
) {
}