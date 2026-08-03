package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

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
    var filter = new JwtAuthenticationFilter(provider, username -> user);
    var request = new MockHttpServletRequest();
    request.addHeader(
        HttpHeaders.AUTHORIZATION,
        "Bearer " + provider.generateAccessToken(user)
    );

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
        .isEqualTo("tester");
  }
}
