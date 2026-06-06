package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.*;

public record UserCreateRequest(

        @NotBlank(message="사용자 이름은 필수입니다.")
        @Size(min = 2, max = 20, message="닉네임은 2글자 이상, 20자 미만이어야 합니다.")
        String username,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이어야합니다.")
        @Size(max=100, message="이메일은 100자 이하여야 합니다.")
        String email,

        @NotBlank
        @Pattern(
                regexp = "^[A-Za-z0-9_!@#$%^&*()]{8,20}$",
                message = "비밀번호는 숫자, 문자, 특수문자를 포함해야 합니다"
        )
        @Size(min=8, max=20)
        String password
) {

}
