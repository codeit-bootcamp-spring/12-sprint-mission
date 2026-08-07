package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

  @Autowired
  protected SessionRegistry sessionRegistry;

  @Mapping(target = "online", expression = "java(isOnline(user.getId()))")
  public abstract UserDto toDto(User user);

  protected boolean isOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .filter(principal -> principal.getUserDto().id().equals(userId))
        .anyMatch(principal -> !sessionRegistry.getAllSessions(principal, false).isEmpty());
  }
}
