package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @Size(min=2, max=10)
        String newUsername,

        @Email
        @Size(max=100, message="이메일은 100자 이하여야 합니다.")
        String newEmail,

        @Pattern(
                regexp = "^[A-Za-z0-9_!@#$%^&*()]{8,20}$",
                message = "비밀번호는 숫자, 문자, 특수문자를 포함해야 합니다"
        )
        @Size(min=8, max=20)
        String newPassword
) {

}
