package com.sprint.mission.discodeit.controller;

import java.time.Instant;
import java.util.*;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserAlreadyException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.util.BinaryContentRequestConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> create(
            @ModelAttribute UserCreateRequest request,
            @RequestParam(value = "profile", required = false) MultipartFile profile) {

        try {
            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest = BinaryContentRequestConverter.convert(profile);
            User user = userService.create(request, optionalProfileCreateRequest);

            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (UserAlreadyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @RequestMapping(value = "/{userId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<User> update(@PathVariable UUID userId,
                                       @ModelAttribute UserUpdateRequest request,
                                       @RequestParam(value = "profile", required = false) MultipartFile profile) {
        try {
            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest = BinaryContentRequestConverter.convert(profile);

            User user = userService.update(userId, request, optionalProfileCreateRequest);

            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        try {
            userService.delete(userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> userDtos = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> find(@PathVariable UUID userId) {
        try {
            UserDto userDto = userService.find(userId);
            return ResponseEntity.status(HttpStatus.OK).body(userDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/updatestatus/{userId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<UserStatus> updateUserStatus(@PathVariable UUID userId) {
        try {
            UserStatus status = userStatusService.updateByUserId(userId, new UserStatusUpdateRequest(Instant.now()));
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}