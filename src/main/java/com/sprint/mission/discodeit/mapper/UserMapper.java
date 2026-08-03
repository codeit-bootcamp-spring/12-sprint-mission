package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

// uses = BinaryContentMapper.class: profile(BinaryContent→BinaryContentDto) 자동 위임
@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

  @Autowired
  protected JwtRegistry jwtRegistry;

  // online: entity에 없는 필드 → 유효한 토큰 보유 여부로 계산
  @Mapping(target = "online", expression = "java(isOnline(user.getId()))")
  public abstract UserDto toDto(User user);

  // 세션이 없는 토큰 기반 인증에서는 레지스트리에 만료되지 않은 엑세스 토큰이 있는지로 접속 여부를 판단
  protected boolean isOnline(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }
}
