package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "online", source = "user", qualifiedByName = "isOnline")
  UserResponse toResponse(User user, @Context SessionRegistry sessionRegistry);

  @Named("isOnline")
  default boolean isOnline(User user, @Context SessionRegistry sessionRegistry) {
    return sessionRegistry.getAllPrincipals().stream()
            .filter(p -> p instanceof DiscodeitUserDetails d
                    && d.getUserDto().id().equals(user.getId()))
            .anyMatch(p -> !sessionRegistry.getAllSessions(p, false).isEmpty());
  }
}