package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentStorageException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
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
                throw new BinaryContentStorageException("READ_PROFILE_FILE", e);
            }
        }

        log.info(
                "User create API requested. username={}, email={}, hasProfile={}",
                userCreateRequest.username(),
                userCreateRequest.email(),
                binaryContent.isPresent()
        );

        UserResponse userResponse = userService.create(userCreateRequest, binaryContent);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID userId) {
        UserResponse userResponse = userService.findById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> userResponseList = userService.findAll();
        return ResponseEntity.ok(userResponseList);
    }

    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
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
                throw new BinaryContentStorageException("READ_PROFILE_FILE", e);
            }
        }

        log.info(
                "User update API requested. userId={}, hasProfile={}",
                userId,
                binaryContent.isPresent()
        );

        UserResponse userResponse = userService.update(userId, userUpdateRequest, binaryContent);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        log.warn("User delete API requested. userId={}", userId);

        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

}