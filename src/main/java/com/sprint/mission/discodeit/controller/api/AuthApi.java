package com.sprint.mission.discodeit.controller.api;

import io.swagger.v3.oas.annotations.Operation;
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
}
