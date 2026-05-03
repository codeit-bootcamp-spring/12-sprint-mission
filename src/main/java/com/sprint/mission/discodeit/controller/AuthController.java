package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final UserStatusService userStatusService;

  @PostMapping("login")
  public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
    User user = null;

    try {
      user = authService.login(loginRequest);
    } catch (Exception e) {
      throw new RuntimeException("로그인 실패 : " + e.getMessage());
    }

    userStatusService.updateByUserId(user.getId(), new UserStatusUpdateRequest(Instant.now()));

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }
}
