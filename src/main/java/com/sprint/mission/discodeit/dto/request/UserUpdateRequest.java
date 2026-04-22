package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {
    @Nullable
    private final String newUsername;
    @Nullable
    private final String newEmail;
    @Nullable
    private final String newPassword;

    @Nullable
    private final BinaryContentCreateRequest newProfileImage;

    public BinaryContentCreateRequest getProfileImage() {
        return null;
    }
}
