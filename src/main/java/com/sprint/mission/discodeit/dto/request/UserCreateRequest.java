package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

    @NotBlank(message = "이름은 필수입니다.")
    String username,
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,
    @Size(min = 8, message = "비밀번호는 8글자 이상이어야 합니다.")
    String password) {


}

