package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtLogoutHandlerTest {

  @Test
  void deletesRefreshTokenCookie() {
    JwtRegistry registry = new InMemoryJwtRegistry(1);
    String refreshToken = "refresh-token";
    registry.registerJwtInformation(new JwtInformation(
        new UserDto(UUID.randomUUID(), "tester", "tester@example.com", null, true, Role.USER),
        "access-token",
        refreshToken
    ));
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(new Cookie("REFRESH_TOKEN", refreshToken));
    MockHttpServletResponse response = new MockHttpServletResponse();

    new JwtLogoutHandler(registry).logout(request, response, null);

    assertThat(registry.hasActiveJwtInformationByRefreshToken(refreshToken)).isFalse();
    assertThat(response.getCookie("REFRESH_TOKEN")).isNotNull();
    assertThat(response.getCookie("REFRESH_TOKEN").getMaxAge()).isZero();
    assertThat(response.getCookie("REFRESH_TOKEN").getPath()).isEqualTo("/");
  }
}
