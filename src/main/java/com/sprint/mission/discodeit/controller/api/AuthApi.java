package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

// 로그인/로그아웃은 SecurityFilterChain의 필터가 처리하므로 Controller 메소드가 없다.
@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "203", description = "CSRF 토큰이 쿠키(XSRF-TOKEN)로 발급됨")
  })
  ResponseEntity<Void> getCsrfToken(@Parameter(hidden = true) CsrfToken csrfToken);

  @Operation(summary = "엑세스 토큰 재발급",
      description = "쿠키(REFRESH_TOKEN)의 리프레시 토큰으로 토큰 쌍을 재발급한다. 리프레시 토큰은 로테이션된다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "재발급 성공",
          content = @Content(schema = @Schema(implementation = JwtDto.class))
      ),
      @ApiResponse(responseCode = "401", description = "리프레시 토큰이 없거나 유효하지 않음")
  })
  ResponseEntity<JwtDto> refresh(@Parameter(hidden = true) String refreshToken);

  @Operation(summary = "사용자 권한 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "권한 수정 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(responseCode = "403", description = "ADMIN 권한이 없음"),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
  })
  ResponseEntity<UserDto> updateRole(
      @Parameter(description = "권한 수정 정보") UserRoleUpdateRequest request);
}
