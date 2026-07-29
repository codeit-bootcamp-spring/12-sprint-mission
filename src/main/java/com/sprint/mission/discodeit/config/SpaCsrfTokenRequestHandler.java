package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.function.Supplier;

import org.springframework.security.web.csrf.*;
import org.springframework.util.StringUtils;

/**
 * Spring Security의 CsrfFilter가 CSRF 토큰을 다룰 때 사용하는 전략 클래스
 */
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  // 토큰 원문 그대로 사용
  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
  // 토큰 XOR로 마스킹해 응답에 노출
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  /**
   * 응답에 쓸 CSRF 토큰을 준비하고 쿠키 발급 유도
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      Supplier<CsrfToken> csrfToken
  ) {
    xor.handle(request, response, csrfToken);
    csrfToken.get();
  }

  /**
   * 요청의 CSRF 토큰 값을 어떤 방식으로 읽을지 정하는 메서드 헤더가 있는 경우 plain 방식으로 읽고 헤더가 없다면 xor 방식으로 읽고 복원
   */
  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    String headerValue = request.getHeader(csrfToken.getHeaderName());
    return (StringUtils.hasText(headerValue) ? plain : xor)
        .resolveCsrfTokenValue(request, csrfToken);
  }
}
