package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank @Size(min = 2, max = 50) String newUsername,
    @NotBlank @Email @Size(max = 100) String newEmail,
    @NotBlank @Size(min = 8, max = 60) String newPassword
) {}
