package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.SessionManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  private SessionManager sessionManager;

  @Mapping(target = "online", expression = "java(isOnline(user))")
  public abstract UserDto toDto(User user);

  protected boolean isOnline(User user) {
    return !sessionManager.getActiveSessionByUserId(user.getId()).isEmpty();
  }
}
