package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청: {}", csrfToken.getToken());
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  @Override
  @GetMapping("me")
  public ResponseEntity<UserDto> getCurrentUser(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    return userDetails == null
        ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        : ResponseEntity.ok(userDetails.getUserDto());
  }
}
