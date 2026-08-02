package com.sprint.mission.discodeit.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

// 인증이 필요한 요청을 테스트할 때 DiscodeitUserDetails를 Principal로 주입하기 위한 헬퍼
public final class SecurityTestSupport {

  private SecurityTestSupport() {
  }

  public static RequestPostProcessor asUser(UUID userId, String username, Role role) {
    UserDto userDto = new UserDto(userId, username, username + "@email.com", null, true, role);
    return user(new DiscodeitUserDetails(userDto, "encoded-password"));
  }

  public static RequestPostProcessor asUser(String userId, String username, Role role) {
    return asUser(UUID.fromString(userId), username, role);
  }
}
