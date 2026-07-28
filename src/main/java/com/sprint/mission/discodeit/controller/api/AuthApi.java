package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "현재 사용자 정보 조회")
  @ApiResponse(responseCode = "200", description = "현재 사용자 정보 조회 성공")
  ResponseEntity<UserDto> getCurrentUser(
      @Parameter(hidden = true) DiscodeitUserDetails userDetails
  );
}
