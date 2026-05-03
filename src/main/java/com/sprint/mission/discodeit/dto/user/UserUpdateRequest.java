package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateRequest(
    UUID id,
    String newUsername,
    String newEmail,
    String newPassword,
    UUID profileImageId
) {

}
