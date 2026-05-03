package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final BinaryContentService binaryContentService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> create(
      @RequestPart(value = "userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
//    System.out.println(request.newEmail() + "\n"
//        + request.profileImageId() + "\n"
//        + profile);
    UUID profileId = request.profileImageId();

    if (profile != null && profileId == null) {
      try {
        profileId = upload(profile);
      } catch (IOException e) {
        throw new RuntimeException("프로필 업로드 실패: " + e.getMessage());
      }
    }

//    System.out.println(profileId);

    UserCreateRequest serviceRequest = new UserCreateRequest(
        request.username(),
        request.email(),
        request.password(),
        profileId
    );

    User user = userService.create(serviceRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  // PATCH /api/participantIds/{userId}
  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UUID profileId = request.profileImageId();

    if (profile != null && profileId == null) {
      try {
        profileId = upload(profile);
      } catch (IOException e) {
        throw new RuntimeException("프로필 업로드 실패: " + e.getMessage());
      }
    }

    System.out.println(userId +
        request.newUsername() +
        request.newEmail() +
        request.newPassword() +
        profileId);

    UserUpdateRequest serviceRequest = new UserUpdateRequest(
        userId,
        request.newUsername(),
        request.newEmail(),
        request.newPassword(),
        profileId
    );

    // 서비스 로직에서 userId를 활용하도록 전달
    UserDto updatedUser = userService.update(serviceRequest);
    return ResponseEntity.ok(updatedUser);
  }

  // DELETE /api/participantIds/{userId}
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build(); // 204 No Content
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> find(@PathVariable UUID userId) {
    UserDto user = userService.find(userId);
    return ResponseEntity.status(HttpStatus.OK).body(user); // 204 No Content
  }

  // 나중에 수정
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  // PATCH /api/participantIds/{userId}/userStatus
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    UserStatus status = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.ok(status);
  }

  public UUID upload(MultipartFile file)
      throws IOException {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("업로드된 프로필 파일이 없습니다.");
    }

    BinaryContentCreateRequest request = new BinaryContentCreateRequest(
        file.getOriginalFilename(),
        file.getContentType(),
        file.getSize(),
        file.getBytes()
    );

    BinaryContent savedContent = binaryContentService.create(request);

    return savedContent.getId();
  }
}
