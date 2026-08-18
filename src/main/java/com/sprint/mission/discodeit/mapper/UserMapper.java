package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserOnlineChecker;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

// uses = BinaryContentMapper.class: profile(BinaryContent→BinaryContentDto) 자동 위임
@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

  @Autowired
  protected UserOnlineChecker userOnlineChecker;

  // online: entity에 없는 필드 → 유효한 토큰 보유 여부로 계산
  @Mapping(target = "online", expression = "java(isOnline(user.getId()))")
  public abstract UserDto toDto(User user);

  // 판단 기준(세션/토큰)은 UserOnlineChecker가 알고 있고, 매퍼는 결과만 받아 채운다
  protected boolean isOnline(UUID userId) {
    return userOnlineChecker.isOnline(userId);
  }
}
