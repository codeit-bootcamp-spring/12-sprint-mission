package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import java.util.UUID;

public interface AuthService {

  UserDto me(UUID userId);

  UserDto updateRole(UserRoleUpdateRequest request);

  /** 리프레시 토큰으로 토큰 쌍을 재발급한다. 반환되는 JwtInformation은 레지스트리 항목이 아닌 응답용 값이다. */
  JwtInformation refresh(String refreshToken);
}
