package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateApiRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateApiRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public User createUser(@RequestBody UserCreateApiRequest request) {
        return userService.create(
                request.user(),
                Optional.ofNullable(request.profile())
        );
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public User updateUser(@PathVariable UUID userId,
                              @RequestBody UserUpdateApiRequest request) {
        return userService.update(
                userId,
                request.user(),
                Optional.ofNullable(request.profile())
        );
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> findAllUsers() {
        return userService.findAll();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserDto findUser(@PathVariable UUID userId) {
        return userService.find(userId);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public UserStatus updateUserStatus(@PathVariable UUID userId,
                                       @RequestBody UserStatusUpdateRequest request) {
        return userStatusService.updateByUserId(userId, request);
    }
}
