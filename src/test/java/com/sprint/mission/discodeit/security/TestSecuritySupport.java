package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public final class TestSecuritySupport {

  private TestSecuritySupport() {
  }

  public static UserDto authenticate(UUID userId, Role role) {
    UserDto userDto = new UserDto(
        userId,
        "test-user",
        "test@test.com",
        null,
        true,
        role
    );
    DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "password");
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            principal,
            principal.getPassword(),
            principal.getAuthorities()
        );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return userDto;
  }

  public static void clear() {
    SecurityContextHolder.clearContext();
  }
}
