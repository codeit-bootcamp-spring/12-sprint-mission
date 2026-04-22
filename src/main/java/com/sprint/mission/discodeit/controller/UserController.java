package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest userCreateRequest) {
        UserResponse userResponse = userService.create(userCreateRequest, Optional.empty());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> userResponseList = userService.findAll();
        return ResponseEntity.ok(userResponseList);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH, consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @RequestPart("request") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UserResponse userResponse = userService.update(id, userUpdateRequest, profile);
        return ResponseEntity.ok(userResponse);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/online/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusResponse> updateOnlineStatus(
            @PathVariable UUID id,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        UserStatusResponse userStatusResponse = userStatusService.updateByUserId(id, userStatusUpdateRequest);
        return ResponseEntity.ok(userStatusResponse);
    }
}