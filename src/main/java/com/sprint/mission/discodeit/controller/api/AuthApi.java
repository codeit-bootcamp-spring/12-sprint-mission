package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "Auth API")
public interface AuthApi {

  @Operation(summary = "Log-in")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Log-in Successful",
          content = @Content(schema = @Schema(implementation = User.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Unable to find user",
          content = @Content(examples = @ExampleObject(value = "User with username {username} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "incorrect password",
          content = @Content(examples = @ExampleObject(value = "Wrong password"))
      )
  })
  ResponseEntity<UserDto> login(
      @Parameter(description = "Login info") LoginRequest loginRequest
  );
} 