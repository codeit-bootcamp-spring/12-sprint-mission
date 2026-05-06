package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> binaryContent = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            try {
                binaryContent = Optional.of(new BinaryContentCreateRequest(
                        profile.getBytes(),
                        profile.getOriginalFilename(),
                        profile.getContentType()
                ));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read profile file", e);
            }
        }
        UserResponse userResponse = userService.create(userCreateRequest, binaryContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> userResponseList = userService.findAll();
        return ResponseEntity.ok(userResponseList);
    }

    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> binaryContent = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            try {
                binaryContent = Optional.of(new BinaryContentCreateRequest(
                        profile.getBytes(),
                        profile.getOriginalFilename(),
                        profile.getContentType()
                ));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read profile file", e);
            }
        }

        UserResponse userResponse = userService.update(userId, userUpdateRequest, binaryContent);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusResponse> updateOnlineStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        UserStatusResponse userStatusResponse = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return ResponseEntity.ok(userStatusResponse);
    }
}