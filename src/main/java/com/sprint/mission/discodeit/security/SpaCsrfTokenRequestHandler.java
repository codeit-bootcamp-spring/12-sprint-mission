package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

/**
 * CSR + SPA 환경에 맞춘 CSRF 토큰 핸들러. (Spring Security 공식 문서 권장 구현)
 *
 * <p>디폴트 구현체인 {@link XorCsrfTokenRequestAttributeHandler}만 쓰면 쿠키에 담긴 원본 토큰과
 * 헤더로 올라온 토큰의 형태가 달라 검증에 실패한다.
 */
public final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      Supplier<CsrfToken> csrfToken) {
    // 응답 본문에 토큰이 노출되는 경우를 대비해 BREACH 방어(xor)를 적용
    this.xor.handle(request, response, csrfToken);
    // 토큰은 지연 로딩이므로, 여기서 한 번 꺼내야 쿠키로 내려간다
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    // 헤더로 온 값은 쿠키의 원본 토큰(plain), 폼 파라미터로 온 값은 xor 인코딩된 토큰
    String headerValue = request.getHeader(csrfToken.getHeaderName());
    return (StringUtils.hasText(headerValue) ? this.plain : this.xor)
        .resolveCsrfTokenValue(request, csrfToken);
  }
}
