package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 디폴트 구현체(SavedRequestAwareAuthenticationSuccessHandler)를 대체한다.
 *
 * <p>디폴트는 인증 성공 시 원래 요청 URL로 리다이렉트하지만, CSR 환경에서는 리다이렉트 대신
 * 200 UserDto를 그대로 내려줘야 한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    log.info("로그인 성공: username={}", userDetails.getUsername());

    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(response.getWriter(), userDetails.getUserDto());
  }
}
