package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {

    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie ->
              cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
          .findFirst()
          .ifPresent(cookie -> {
            String refreshToken = cookie.getValue();
            UUID userId = jwtTokenProvider.getUserId(refreshToken);

            jwtRegistry.invalidateJwtInformationByUserId(userId);
          });
    }

    Cookie refreshTokenCookie =
        jwtTokenProvider.generateRefreshTokenExpirationCookie();

    response.addCookie(refreshTokenCookie);
  }
}