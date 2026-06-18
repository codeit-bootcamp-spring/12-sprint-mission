package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
	@NotBlank
	@Size(min = 2, max = 12, message = "사용자 이름은 2자 이상 12자 이하여야 합니다.")
    String username,
	@NotBlank
	@Email
    String email,
	@NotBlank
	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
		message = "비밀번호는 영문과 숫자를 포함하여 최소 8자 이상이어야 합니다."
	)
    String password
) {

}
