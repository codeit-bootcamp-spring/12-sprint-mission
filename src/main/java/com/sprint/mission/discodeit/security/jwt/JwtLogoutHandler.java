package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;

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

            if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
              UUID userId = jwtTokenProvider.getUserId(refreshToken); // 정상 일때만 userId 추출
              jwtRegistry.invalidateJwtInformationByUserId(userId);
              Optional.ofNullable(cacheManager.getCache("users"))
                  .ifPresent(cache -> cache.evict("all"));
            }
          });
    }

    Cookie refreshTokenCookie =
        jwtTokenProvider.generateRefreshTokenExpirationCookie();

    response.addCookie(refreshTokenCookie);
  }
}