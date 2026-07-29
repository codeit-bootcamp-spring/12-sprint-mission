package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  public static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";
  private static final String MDC_REQUEST_ID = "requestId";
  private static final String MDC_REQUEST_METHOD = "requestMethod";
  private static final String MDC_REQUEST_URI = "requestUri";

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) {
    // 로그 출력 예시가 8자라 예시에 맞게 설정함
    String requestId = UUID.randomUUID().toString().substring(0, 8);

    // 클라이언트가 받은 헤더 값으로 서버 로그 바로 역추적 가능해
    // 실무에서 장애 문의 대응할 때 해당 패턴 많이 사용함
    MDC.put(MDC_REQUEST_ID, requestId);
    MDC.put(MDC_REQUEST_METHOD, request.getMethod());
    MDC.put(MDC_REQUEST_URI, request.getRequestURI());

    response.setHeader(REQUEST_ID_HEADER, requestId);

    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      Exception ex
  ) {
    MDC.clear();
  }
}
