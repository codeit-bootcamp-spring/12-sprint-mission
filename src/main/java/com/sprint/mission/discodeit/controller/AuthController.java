package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.TokenRefreshResult;
import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final UserService userService;
  private final AuthService authService;

  @Override
  @PostMapping("/api/auth/refresh")
  public ResponseEntity<JwtDto> refresh(
      @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletRequest request
  ) {
    TokenRefreshResult refreshResult = authService.refresh(refreshToken);
    ResponseCookie rotatedRefreshToken = ResponseCookie.from("REFRESH_TOKEN",
            refreshResult.refreshToken())
        .httpOnly(true)
        .secure(request.isSecure())
        .path("/")
        .sameSite("Strict")
        .maxAge(Duration.ofMillis(refreshResult.refreshTokenExpiration()))
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, rotatedRefreshToken.toString())
        .body(refreshResult.jwtDto());
  }

  @Override
  @PutMapping("/api/auth/role")
  public ResponseEntity<UserResponse> updateRole(
      @Valid @RequestBody UserRoleUpdateRequest request
  ) {
    return ResponseEntity.ok(userService.updateRole(request));
  }
}
