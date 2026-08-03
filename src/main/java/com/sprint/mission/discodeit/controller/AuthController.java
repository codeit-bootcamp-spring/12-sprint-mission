package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;

  // CsrfToken은 HandlerMethodArgumentResolver가 자동으로 주입해준다.
  // GET은 CSRF 검증 대상이 아니라 토큰이 초기화되지 않으므로, 여기서 명시적으로 꺼내 쿠키 발급을 유도한다.
  @GetMapping(path = "csrf-token")
  @Override
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청: {}", csrfToken.getToken());
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }


  // 엑세스 토큰이 없거나 만료된 상태에서 호출되므로 인증을 요구하지 않는다.
  // 신원 확인은 HttpOnly 쿠키에 담긴 리프레시 토큰으로만 한다.
  @PostMapping(path = "refresh")
  @Override
  public ResponseEntity<JwtDto> refresh(
      @CookieValue(name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, required = false)
      String refreshToken) {
    JwtInformation jwtInformation = authService.refresh(refreshToken);
    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE,
            jwtTokenProvider.createRefreshTokenCookie(jwtInformation.getRefreshToken()).toString())
        .body(new JwtDto(jwtInformation.getUserDto(), jwtInformation.getAccessToken()));
  }

  @PutMapping(path = "role")
  @Override
  public ResponseEntity<UserDto> updateRole(@RequestBody @Valid UserRoleUpdateRequest request) {
    log.debug("PUT /api/auth/role - userId={}, newRole={}", request.userId(), request.newRole());
    UserDto user = authService.updateRole(request);
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }
}
