package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;

  public JwtLogoutHandler(JwtRegistry jwtRegistry) {
    this.jwtRegistry = jwtRegistry;
  }

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
          .findFirst()
          .ifPresent(cookie -> jwtRegistry.invalidateJwtInformationByRefreshToken(cookie.getValue()));
    }

    ResponseCookie expiredRefreshToken = ResponseCookie.from(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, "")
        .httpOnly(true)
        .secure(request.isSecure())
        .path("/")
        .sameSite("Strict")
        .maxAge(Duration.ZERO)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshToken.toString());
  }
}
