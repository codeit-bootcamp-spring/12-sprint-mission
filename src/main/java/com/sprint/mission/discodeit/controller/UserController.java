package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<User> create(
      @RequestPart UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.ofNullable(profile)
        .map(this::resolveProfileRequest);
    User user = userService.create(userCreateRequest, profileCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<User> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.ofNullable(profile)
        .map(this::resolveProfileRequest);
    User user = userService.update(userId, userUpdateRequest, profileCreateRequest);
    return ResponseEntity.ok(user);
  }

  @DeleteMapping("/{userId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping()
  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity.ok(users);
  }

  @PatchMapping("/{userId}/userStatus")
  @Override
  public ResponseEntity<UserStatus> updateStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.ok(userStatus);
  }

  private BinaryContentCreateRequest resolveProfileRequest(MultipartFile file) {
    if (file.isEmpty()) {
      return null;
    }
    try {
      return new BinaryContentCreateRequest(file.getOriginalFilename(), file.getContentType(),
          file.getBytes());
    } catch (IOException e) {
      throw new RuntimeException("프로필 이미지 파일을 읽는 중 오류가 발생했습니다.", e);
    }
  }
}
