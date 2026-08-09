package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * 디폴트 구현체(SimpleUrlAuthenticationFailureHandler)를 대체한다.
 *
 * <p>디폴트는 실패 시 /login?error로 리다이렉트하므로, CSR 환경에 맞게 401 ErrorResponse를 반환한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    // username 파라미터가 아예 없을 수도 있으므로 null 방어 (Map.of는 null 값을 허용하지 않음)
    String username = request.getParameter("username") == null ? ""
        : request.getParameter("username");
    log.warn("로그인 실패: username={}, reason={}", username, exception.getMessage());

    // 사용자 없음 / 비밀번호 불일치를 구분하지 않고 동일한 응답을 반환 (계정 존재 여부 노출 방지)
    ErrorResponse errorResponse = ErrorResponse.of(
        new InvalidCredentialsException(username), HttpStatus.UNAUTHORIZED.value());

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
