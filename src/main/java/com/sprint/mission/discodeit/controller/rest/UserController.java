package com.sprint.mission.discodeit.controller.rest;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.file.FileProcessingException;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> createWithImage(
      @RequestPart("userCreateRequest") @Valid UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    log.debug("사용자 생성 API 요청: username={}, profileIncluded={}, profileSize={}",
        userCreateRequest.username(),
        profile != null && !profile.isEmpty(),
        profile == null ? 0 : profile.getSize());

    Optional<BinaryContentCreateRequest> profileImageRequest = Optional.ofNullable(profile)
        .flatMap(this::toBinaryContentCreateRequest);

    UserDto createdUser = userService.create(userCreateRequest, profileImageRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAllWithFetch());
  }

  @PatchMapping(
      value = "/{userId}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<UserDto> updateUser(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") @Valid UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    log.debug(
        "사용자 수정 API 요청: userId={}, usernameIncluded={}, emailIncluded={}, passwordIncluded={}, profileIncluded={}, profileSize={}",
        userId,
        userUpdateRequest.newUsername() != null,
        userUpdateRequest.newEmail() != null,
        userUpdateRequest.newPassword() != null,
        profile != null && !profile.isEmpty(),
        profile == null ? 0 : profile.getSize()
    );

    Optional<BinaryContentCreateRequest> profileImageRequest = Optional.ofNullable(profile)
        .flatMap(this::toBinaryContentCreateRequest);

    UserDto updatedUser = userService.update(userId, userUpdateRequest, profileImageRequest);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(
      @PathVariable UUID userId
  ) {
    log.debug("사용자 삭제 API 요청: userId={}", userId);

    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  private Optional<BinaryContentCreateRequest> toBinaryContentCreateRequest(MultipartFile profile) {
    if (profile == null || profile.isEmpty()) {
      return Optional.empty();
    }

    try {
      return Optional.of(
          new BinaryContentCreateRequest(
              profile.getOriginalFilename(),
              profile.getContentType(),
              profile.getBytes()
          )
      );
    } catch (IOException e) {
      throw new FileProcessingException(e);
    }
  }
}

