package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();


  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();


  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      Supplier<CsrfToken> csrfToken) {
    // 응답게 토큰이 노출될 때마다 BREACH 공격을 방지하도록 XOR 방식 사용
    this.xor.handle(request, response, csrfToken);

    // 지연 생성된 CSRF 토큰을 실제로 생성하고 쿠키에 저장
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    String headerValue = request.getHeader(csrfToken.getHeaderName());

    // SPA가 헤더로 보낸 토큰은 쿠키에 저장된 원본 토큰이므로 plain 방식으로 읽음
    // 요청 파라미터로 전달된 경우에는 XOR 방식으로 읽음
    CsrfTokenRequestHandler handler = StringUtils.hasText(headerValue) ? this.plain : this.xor;

    return handler.resolveCsrfTokenValue(request, csrfToken);
  }
}
