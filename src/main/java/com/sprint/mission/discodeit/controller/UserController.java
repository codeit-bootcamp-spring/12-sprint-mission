package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<UserDto> create(
      @RequestPart @Valid UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    log.debug("User 생성 요청: userCreateRequest={}, profile={}", userCreateRequest,
        profile != null ? profile.getOriginalFilename() : "null");
    Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.ofNullable(profile)
        .map(this::resolveProfileRequest);
    UserDto user = userService.create(userCreateRequest, profileCreateRequest);
    log.info("User 생성 응답: userId={}", user.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    log.debug("User 수정 요청: userId={}, userUpdateRequest={}, profile={}", userId, userUpdateRequest,
        profile != null ? profile.getOriginalFilename() : "null");
    Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.ofNullable(profile)
        .map(this::resolveProfileRequest);
    UserDto user = userService.update(userId, userUpdateRequest, profileCreateRequest);
    log.info("User 수정 응답: userId={}", userId);
    return ResponseEntity.ok(user);
  }

  @DeleteMapping("/{userId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    log.debug("User 삭제 요청: userId={}", userId);
    userService.delete(userId);
    log.info("User 삭제 응답: userId={}", userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping()
  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    log.debug("User 목록 조회 요청");
    List<UserDto> users = userService.findAll();
    log.info("User 목록 조회 응답: userCount={}", users.size());
    return ResponseEntity.ok(users);
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
