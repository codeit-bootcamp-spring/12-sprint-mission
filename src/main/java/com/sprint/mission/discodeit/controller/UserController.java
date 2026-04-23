package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<UserDto> createUser (
            @RequestBody UserCreateRequest request,
            @RequestBody(required = false)BinaryContentCreateRequest profile) {
        User user = userService.create(request, Optional.ofNullable(profile));
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
}
