package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;

  // CsrfToken은 HandlerMethodArgumentResolver가 자동으로 주입해준다.
  // GET은 CSRF 검증 대상이 아니라 토큰이 초기화되지 않으므로, 여기서 명시적으로 꺼내 쿠키 발급을 유도한다.
  @GetMapping(path = "csrf-token")
  @Override
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청: {}", csrfToken.getToken());
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  // 인증에 성공한 요청이면 SecurityFilterChain이 세션의 인증 정보를 Principal로 주입해준다
  @GetMapping(path = "me")
  @Override
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UserDto user = authService.me(userDetails.getUserDto().id());
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }
}
