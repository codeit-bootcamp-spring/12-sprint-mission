package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "사용자 아이디 필수입니다.")
    @Size(min = 3, max = 50, message = "사용자 아이디는 3자 이상 50자 이하로 입력해주세요.")
    String username,
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 60, message = "비밀번호는 60자 이하로 입력해주세요.")
    String password
) {

}
