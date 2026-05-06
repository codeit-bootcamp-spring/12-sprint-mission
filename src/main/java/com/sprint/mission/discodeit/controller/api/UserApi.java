package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
@RequestMapping("/api/users")
public interface UserApi {

  @Operation(summary = "Register User")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "User Registered Successfully",
          content = @Content(schema = @Schema(implementation = UserDto.class))  // ✅ UserDto로 수정
      ),
      @ApiResponse(
          responseCode = "400", description = "User with email already exists",
          content = @Content(examples = @ExampleObject(value = "User with email {email} already exists"))
      ),
  })
  ResponseEntity<UserDto> create(
      @Parameter(
          description = "User Info",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) UserCreateRequest userCreateRequest,
      @Parameter(
          description = "User Profile Image",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) MultipartFile profile
  );

  @Operation(summary = "User Info Edit")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User Info Updated Successfully",
          content = @Content(schema = @Schema(implementation = UserDto.class))  // ✅ UserDto로 수정
      ),
      @ApiResponse(
          responseCode = "404", description = "User with id not found",
          content = @Content(examples = @ExampleObject("User with id {userId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "User with email already exists",
          content = @Content(examples = @ExampleObject("user with email {newEmail} already exists"))
      )
  })
  ResponseEntity<UserDto> update(
      @Parameter(description = "Edit User ID") UUID userId,
      @Parameter(description = "Edit User Info") UserUpdateRequest userUpdateRequest,
      @Parameter(description = "Edit User Profile Image") MultipartFile profile
  );

  @Operation(summary = "User Delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User Deleted Successfully"),
      @ApiResponse(
          responseCode = "404", description = "User with id not found",
          content = @Content(examples = @ExampleObject(value = "User with id {id} not found"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "Delete User ID") UUID userId
  );

  @Operation(summary = "Find All Users")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "All Users Found",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
      )
  })
  ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "User Online Status Update")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User Online Status Updated Successfully",
          content = @Content(schema = @Schema(implementation = UserStatus.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "UserStatus with userId not found",
          content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found"))
      )
  })
  ResponseEntity<UserStatus> updateUserStatusByUserId(
      @Parameter(description = "Edited User ID") UUID userId,
      @Parameter(description = "Edited User Online Status info") UserStatusUpdateRequest request
  );
}