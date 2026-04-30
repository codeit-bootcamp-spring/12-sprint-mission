package com.sprint.mission.discodeit.dto.request;

public record UserUpdateRequest (
        String newUsername,
        String newPassword,
        String newEmail
){
}
