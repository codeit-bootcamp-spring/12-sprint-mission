package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 관리 API")
public interface AuthApi {

  @Operation(summary = "로그인")
  @PostMapping("login")
  ResponseEntity<User> login(@RequestBody LoginRequest loginRequest);
}