package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;

@Tag(name = "Auth", description = "Auth API")
public interface AuthApi {

  @Operation(summary = "액세스 토큰 재발급")
  @ApiResponse(responseCode = "200", description = "액세스 토큰 재발급 성공")
  @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
  ResponseEntity<JwtDto> refresh(
      @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletRequest request
  );

  @Operation(summary = "사용자 권한 변경")
  @ApiResponse(responseCode = "200", description = "사용자 권한 변경 성공")
  ResponseEntity<UserResponse> updateRole(UserRoleUpdateRequest request);
}
