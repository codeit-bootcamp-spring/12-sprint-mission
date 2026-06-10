package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
    String newUsername,
    @Email(message = "이메일 형식이 맞지 않습니다.")
    String newEmail,
    String newPassword
) {

}
