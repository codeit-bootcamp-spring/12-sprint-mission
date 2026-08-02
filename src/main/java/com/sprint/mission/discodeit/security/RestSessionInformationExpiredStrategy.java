package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

// 동시 로그인/권한 변경으로 세션이 만료된 경우: 디폴트는 만료 안내 HTML이므로 401 ErrorResponse로 대체
@Slf4j
@RequiredArgsConstructor
@Component
public class RestSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {

  private final ObjectMapper objectMapper;

  @Override
  public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
    log.info("만료된 세션으로 접근: sessionId={}", event.getSessionInformation().getSessionId());

    ErrorResponse errorResponse = new ErrorResponse(
        java.time.Instant.now(),
        "SESSION_EXPIRED",
        "세션이 만료되었습니다. 다시 로그인해 주세요.",
        java.util.Map.of(),
        "SessionInformationExpiredEvent",
        HttpStatus.UNAUTHORIZED.value()
    );

    HttpServletResponse response = event.getResponse();
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
