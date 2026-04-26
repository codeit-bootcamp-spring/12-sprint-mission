package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public User createUser(
            @RequestBody UserCreateRequest userCreateRequest,
            @RequestBody Optional<BinaryContentCreateRequest> profileCreateRequest){
        return userService.create(userCreateRequest, profileCreateRequest);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserDto findUser(@PathVariable UUID userId){
        return userService.find(userId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> findAllUser(){
        return userService.findAll();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public User updateUser(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest userUpdateRequest,
            @RequestBody Optional<BinaryContentCreateRequest> profileCreateRequest){
        return userService.update(userId, userUpdateRequest, profileCreateRequest);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID userId){
        userService.delete(userId);
    }
}
