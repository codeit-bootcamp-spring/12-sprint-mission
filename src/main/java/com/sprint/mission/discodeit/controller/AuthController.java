package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final UserService userService;
  private final UserDetailsService userDetailsService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청: {}", csrfToken.getToken());
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  @PostMapping("refresh")
  public ResponseEntity<?> refresh(
      @CookieValue(
          value = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
          required = false
      ) String refreshToken,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    try {
      if (refreshToken == null
          || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
        throw new BadCredentialsException("Invalid refresh token");
      }
      String username = jwtTokenProvider.consumeRefreshToken(refreshToken);
      DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService
          .loadUserByUsername(username);
      String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
      String rotatedRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
      jwtRegistry.rotateJwtInformation(
          refreshToken,
          new JwtInformation(userDetails.getUserDto(), accessToken, rotatedRefreshToken)
      );

      Cookie cookie = new Cookie(
          JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
          rotatedRefreshToken
      );
      cookie.setHttpOnly(true);
      cookie.setSecure(request.isSecure());
      cookie.setPath("/");
      response.addCookie(cookie);

      return ResponseEntity.ok(new JwtDto(userDetails.getUserDto(), accessToken));
    } catch (AuthenticationException | IllegalArgumentException exception) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ErrorResponse.of(
              HttpStatus.UNAUTHORIZED.value(),
              HttpStatus.UNAUTHORIZED.name(),
              "유효하지 않은 리프레시 토큰입니다."
          ));
    }
  }

  @Override
  @PutMapping("role")
  public ResponseEntity<UserDto> updateRole(
      @Valid @RequestBody UserRoleUpdateRequest request
  ) {
    return ResponseEntity.ok(userService.updateRole(request.userId(), request.newRole()));
  }
}
