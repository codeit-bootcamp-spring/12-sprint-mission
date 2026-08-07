package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

class JwtAuthenticationFilterTest {

  private final JwtTokenProvider provider =
      new JwtTokenProvider("test-environment-jwt-secret-key-32", 60, 120);

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void authenticatesValidBearerAccessToken() throws Exception {
    var user = User.withUsername("tester").password("unused").roles("USER").build();
    var registry = new InMemoryJwtRegistry(1);
    var filter = new JwtAuthenticationFilter(provider, registry, username -> user);
    String accessToken = provider.generateAccessToken(user);
    registry.registerJwtInformation(new JwtInformation(
        new UserDto(UUID.randomUUID(), "tester", "tester@example.com", null, true, Role.USER),
        accessToken,
        provider.generateRefreshToken(user)
    ));
    var request = new MockHttpServletRequest();
    request.addHeader(
        HttpHeaders.AUTHORIZATION,
        "Bearer " + accessToken
    );

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
        .isEqualTo("tester");
  }
}
