package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity
            .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
            .body(null);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest userRoleUpdateRequest){
    log.info("사용자 권한 수정");
    UserDto updatedRoleUser = authService.updateRole(userRoleUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedRoleUser);
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refreshToken(
          @CookieValue (JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
          HttpServletResponse response
  ){
      JwtInformation jwtInformation = authService.refreshToken(refreshToken);
      Cookie cookie = jwtTokenProvider.generateRefreshTokenCookie(jwtInformation.getRefreshToken());
      response.addCookie(cookie);

      JwtDto body = new JwtDto(jwtInformation.getUserDto(), jwtInformation.getAccessToken());
      return ResponseEntity.ok(body);
  }
}
