package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "유저 관리 API")
public interface UserApi {

  @Operation(summary = "User 생성")
  @PostMapping(value = "create")
  ResponseEntity<User> create(
      @RequestPart UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "User 정보 수정")
  @PatchMapping("/{userId}")
  ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "User 삭제")
  @DeleteMapping("/{userId}")
  ResponseEntity<Void> delete(@PathVariable UUID userId);

  @Operation(summary = "모든 User 조회")
  @GetMapping("findAll")
  ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "User 온라인 상태 업데이트")
  @PatchMapping("/{userId}/userStatus")
  ResponseEntity<UserStatus> updateUserStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request
  );
}