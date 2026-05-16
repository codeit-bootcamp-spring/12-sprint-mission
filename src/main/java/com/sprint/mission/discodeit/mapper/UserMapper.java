package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;

  public UserDto toDto(User user) {

    BinaryContentDto profile = null;

    if (user.getProfile() != null) {
      profile = binaryContentMapper.toDto(user.getProfile());
    }

    Boolean online = null;

    if (user.getUserStatus() != null) {
      online = user.getUserStatus().isOnline();
    }

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profile,
        online
    );
  }
}
