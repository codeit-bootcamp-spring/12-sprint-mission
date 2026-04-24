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
import java.time.Instant;
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
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> createUser(
            @RequestPart("request") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        BinaryContentCreateRequest profileRequest = null;

        if (profile != null && !profile.isEmpty()) {
            profileRequest = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );
        }

        User user = userService.create(
                request,
                Optional.ofNullable(profileRequest)
        );

        UserDto savedUser = userService.find(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest request,
            @RequestBody(required = false) BinaryContentCreateRequest profile) {
        User user = userService.update(id, request, Optional.ofNullable(profile));
        UserDto updatedUser = userService.find(user.getId());
        return ResponseEntity.ok(updatedUser);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<UserDto> deleteUser(@PathVariable UUID id) {
        UserDto user = userService.find(id);
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> userList = userService.findAll();
        return ResponseEntity.ok(userList);
    }

    @RequestMapping(value = "/{id}/status", method = RequestMethod.PUT)
    public ResponseEntity<UserStatus> updateUserStatus(@PathVariable UUID id) {
        UserStatus updatedStatus = userStatusService.updateByUserId(
                id,
                new UserStatusUpdateRequest(Instant.now())
        );
        return ResponseEntity.ok(updatedStatus);
    }


}
