package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    BinaryContentResponse profile,
    boolean online,
    UserRole role
) {

  public UserResponse(
      UUID id,
      String username,
      String email,
      BinaryContentResponse profile,
      boolean online
  ) {
    this(id, username, email, profile, online, UserRole.USER);
  }

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile() == null ? null : BinaryContentResponse.from(user.getProfile()),
        user.getStatus().isOnline(),
        user.getRole()
    );
  }
}
