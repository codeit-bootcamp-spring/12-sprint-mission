package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
            @RequestPart UserCreateRequest request,
            @RequestPart(required = false) MultipartFile profile
            ) {
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profile)
                        .filter(file -> !file.isEmpty())
                        .map(file -> {
                            try {
                                return new BinaryContentCreateRequest(
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException("프로필 파일 처리 실패", e);
                            }
                        });

        User user = userService.create(request, profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(
            value = "/update",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(
            @RequestParam UUID userId,
            @RequestPart UserUpdateRequest request,
            @RequestPart(required = false) MultipartFile profile
            ) {
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profile)
                        .filter(file -> !file.isEmpty())
                        .map(file -> {
                            try {
                                return new BinaryContentCreateRequest(
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException("프로필 파일 처리 실패: " + file.getOriginalFilename(), e);
                            }
                        });

        User user = userService.update(userId, request, profileRequest);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @RequestMapping(value = "/updateUserStatusByUserId", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateUserStatusByUserId(
            @RequestParam UUID userId,
            @RequestBody UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(userStatus);
    }
}
