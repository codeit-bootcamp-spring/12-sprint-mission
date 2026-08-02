package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

// uses = BinaryContentMapper.class: profile(BinaryContent→BinaryContentDto) 자동 위임
@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

  @Autowired
  protected SessionRegistry sessionRegistry;

  // online: entity에 없는 필드 → 유효한 세션 보유 여부로 계산
  @Mapping(target = "online", expression = "java(isOnline(user.getId()))")
  public abstract UserDto toDto(User user);

  // UserStatus 엔티티 대신 SessionRegistry에 만료되지 않은 세션이 있는지로 접속 여부를 판단
  protected boolean isOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .filter(userDetails -> userDetails.getUserId().equals(userId))
        .anyMatch(userDetails -> !sessionRegistry.getAllSessions(userDetails, false).isEmpty());
  }
}
