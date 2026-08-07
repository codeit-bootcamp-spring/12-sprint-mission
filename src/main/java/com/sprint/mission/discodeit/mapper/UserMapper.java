package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.JwtRegistry;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

  @Autowired
  protected JwtRegistry jwtRegistry;

  @Mapping(target = "online", expression = "java(isOnline(user.getId()))")
  public abstract UserDto toDto(User user);

  protected boolean isOnline(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }
}
