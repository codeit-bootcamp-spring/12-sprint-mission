package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 세션 기반 LoginSuccessHandler를 대체한다.
 *
 * <p>인증에 성공하면 토큰 한 쌍을 발급해 레지스트리에 등록하고, 엑세스 토큰은 Body로, 리프레시 토큰은
 * HttpOnly 쿠키로 내려준다. 리프레시 토큰을 Body에 담지 않는 이유는 XSS로 탈취되면 재발급을 통해
 * 사실상 무기한 세션을 얻을 수 있기 때문이다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final AuthService authService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    log.info("로그인 성공: username={}", userDetails.getUsername());

    JwtInformation jwtInformation = jwtTokenProvider.generate(userDetails.getUserDto());
    // 응답 UserDto의 online을 계산하려면 레지스트리에 먼저 등록되어 있어야 한다
    // (등록 순서를 뒤집으면 로그인 응답의 online이 항상 false가 된다)
    jwtRegistry.registerJwtInformation(jwtInformation);

    UserDto user = authService.me(userDetails.getUserId());

    response.addHeader(HttpHeaders.SET_COOKIE,
        jwtTokenProvider.createRefreshTokenCookie(jwtInformation.getRefreshToken()).toString());
    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(response.getWriter(),
        new JwtDto(user, jwtInformation.getAccessToken()));
  }
}
