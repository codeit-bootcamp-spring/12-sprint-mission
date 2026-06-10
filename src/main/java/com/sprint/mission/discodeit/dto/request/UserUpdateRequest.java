package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @Size(min = 3, max = 50, message = "사용자 아이디는 3자 이상 50자 이하로 입력해주세요.")
    String newUsername,
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    String newEmail,
    @Size(min = 8, max = 60, message = "비밀번호는 8자 이상 60자 이하로 입력해주세요.")
    String newPassword
) {

}
