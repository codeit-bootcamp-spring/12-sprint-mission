package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserFindDTO {
    private UUID id;
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private boolean isOnline;

    public static UserFindDTO from(User user, UserStatus userStatus) {
        UserFindDTO dto = new UserFindDTO();
        dto.id = user.getId();
        dto.profileId = user.getProfileId();
        dto.createdAt = user.getCreatedAt();
        dto.updatedAt = user.getUpdatedAt();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.isOnline = userStatus.isUserOnline();
        return dto;
    }
}
