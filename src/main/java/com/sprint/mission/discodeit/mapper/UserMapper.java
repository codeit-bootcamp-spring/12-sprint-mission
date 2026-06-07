package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// uses = BinaryContentMapper.class: profile(BinaryContent→BinaryContentDto) 자동 위임
@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface UserMapper {

  // online: entity에 없는 필드 → UserStatus.isOnline() 계산식으로 처리
  @Mapping(target = "online",
      expression = "java(user.getStatus() != null ? user.getStatus().isOnline() : null)")
  UserDto toDto(User user);
}
