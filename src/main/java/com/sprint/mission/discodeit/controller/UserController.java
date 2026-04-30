package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final ObjectMapper objectMapper;

  @RequestMapping(
      method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<User> create(
      @RequestPart("userCreateRequest") String userCreateRequestJson,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    try {
      UserCreateRequest userCreateRequest =
          objectMapper.readValue(userCreateRequestJson, UserCreateRequest.class);

      Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
          .flatMap(this::resolveProfileRequest);

      User createdUser = userService.create(userCreateRequest, profileRequest);

      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(createdUser);
    } catch (IOException e) {
      throw new RuntimeException("User 생성 요청 JSON 파싱 실패", e);
    }
  }

  @RequestMapping(
      path = "/{userId}",
      method = RequestMethod.PATCH,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<User> update(
      @PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") String userUpdateRequestJson,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    try {
      UserUpdateRequest userUpdateRequest =
          objectMapper.readValue(userUpdateRequestJson, UserUpdateRequest.class);

      Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
          .flatMap(this::resolveProfileRequest);

      User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);

      return ResponseEntity
          .status(HttpStatus.OK)
          .body(updatedUser);
    } catch (IOException e) {
      throw new RuntimeException("User 수정 요청 JSON 파싱 실패", e);
    }
  }

  @RequestMapping(path = "/{userId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  @RequestMapping(path = "/{userId}/userStatus", method = RequestMethod.PATCH)
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @PathVariable("userId") UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUserStatus);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile == null || profileFile.isEmpty()) {
      return Optional.empty();
    }

    try {
      BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
          profileFile.getOriginalFilename(),
          profileFile.getContentType(),
          profileFile.getBytes()
      );
      return Optional.of(binaryContentCreateRequest);
    } catch (IOException e) {
      throw new RuntimeException("profile 파일 변환 실패", e);
    }
  }
}