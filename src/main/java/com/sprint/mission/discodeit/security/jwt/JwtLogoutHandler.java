package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * 로그아웃 시 리프레시 토큰을 무효화하고 쿠키를 제거한다.
 *
 * <p>로그아웃 API는 인증을 요구하지 않기 때문에 Authentication이 null일 수 있다. 따라서 누구의
 * 로그아웃인지는 요청 쿠키의 리프레시 토큰으로 판단한다.
 *
 * <p>엑세스 토큰은 만료 전까지 서명상으로는 계속 유효하지만, 레지스트리에서 함께 사라지므로
 * JwtAuthenticationFilter의 상태 검사에서 걸러진다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      Arrays.stream(cookies)
          .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
          .findFirst()
          .ifPresent(cookie -> {
            jwtRegistry.invalidateJwtInformationByRefreshToken(cookie.getValue());
            log.info("로그아웃: 리프레시 토큰 무효화 완료");
          });
    }

    // 서버 상태를 지워도 브라우저에 쿠키가 남아 있으면 만료될 때까지 재발급을 시도하므로 함께 제거한다
    response.addHeader(HttpHeaders.SET_COOKIE,
        jwtTokenProvider.createExpiredRefreshTokenCookie().toString());
  }
}
