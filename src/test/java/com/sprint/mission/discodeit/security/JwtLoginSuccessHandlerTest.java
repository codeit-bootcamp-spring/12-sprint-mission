package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class JwtLoginSuccessHandlerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final JwtTokenProvider provider =
      new JwtTokenProvider("test-environment-jwt-secret-key-32", 60, 120);
  private final JwtLoginSuccessHandler handler =
      new JwtLoginSuccessHandler(objectMapper, provider);

  @Test
  void returnsAccessTokenAndStoresRefreshTokenInCookie() throws Exception {
    UserDto userDto = new UserDto(
        UUID.randomUUID(), "tester", "tester@example.com", null, true, Role.USER
    );
    DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "unused");
    var authentication = UsernamePasswordAuthenticationToken.authenticated(
        principal, null, principal.getAuthorities()
    );
    MockHttpServletResponse response = new MockHttpServletResponse();

    handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication);

    JsonNode body = objectMapper.readTree(response.getContentAsByteArray());
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(body.path("userDto").path("username").asText()).isEqualTo("tester");
    assertThat(provider.validateToken(body.path("accessToken").asText())).isTrue();
    assertThat(response.getCookie("REFRESH_TOKEN")).isNotNull();
    assertThat(response.getCookie("REFRESH_TOKEN").isHttpOnly()).isTrue();
    assertThat(provider.validateToken(response.getCookie("REFRESH_TOKEN").getValue())).isTrue();
  }
}
