package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.SessionManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    uses = {BinaryContentMapper.class}
)
public abstract class UserMapper {

  @Autowired
  protected SessionManager sessionManager;

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  public abstract User toEntity(UserCreateRequest request, BinaryContent profile);

  @Mapping(target = "online", expression = "java(sessionManager.isOnline(user.getId()))")
  public abstract UserDto toDto(User user);
}
