package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

  public static UserStatusResponse from(UserStatus userStatus) {
    return new UserStatusResponse(
        userStatus.getId(),
        userStatus.getUser().getId(),
        userStatus.getLastActiveAt()
    );
  }
}
