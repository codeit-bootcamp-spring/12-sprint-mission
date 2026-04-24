package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.LoginRequestDto;
import com.sprint.mission.discodeit.dto.data.OnlineStatusDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register User
    @RequestMapping(method = RequestMethod.POST)
    public User createUser(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    // Edit User info
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public User updateUser(
            @PathVariable UUID id, @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    // Delete User
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }

    // 심화 요구사항
    @GetMapping("/findAll")
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // Update online status

    @RequestMapping(value = "/{id}/online", method = RequestMethod.PATCH)
    public User updateOnlineStatus(
            @PathVariable UUID id,
            @RequestBody OnlineStatusDto onlineStatusDto
    ) {
        return userService.updateOnlineStatus(id, onlineStatusDto.getOnline());
    }

    // Logging in
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public User login(@RequestBody LoginRequestDto loginDto) {
        return userService.login(loginDto.getEmail(), loginDto.getPassword());
    }


}



