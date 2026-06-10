package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
    @NotBlank(message = "사용자명은 필수입니다.")
    String username,
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 맞지 않습니다.")
    String email,
    @NotBlank(message = "비밀번호는 필수입니다.")
    String password
) {

}
