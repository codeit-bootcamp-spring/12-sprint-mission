package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.LoginRequestDto;
import com.sprint.mission.discodeit.dto.data.OnlineStatusDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
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
  @PostMapping
  public User createUser(@RequestBody UserDto userDto) {
    return userService.create(userDto);
  }

  // Find User by ID
  @GetMapping("/{id}")
  public ResponseEntity<UserDto> findUserById(@PathVariable UUID id) {
    UserDto user = userService.find(id);
    return ResponseEntity.ok(user);
  }

  // Edit User info
  @PutMapping("/{id}")
  public User updateUser(
      @PathVariable UUID id, @RequestBody UserDto userDto) {
    return userService.update(id, userDto);
  }

  // Delete User
  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable UUID id) {
    userService.delete(id);
  }

  // Partial Update User
  @PatchMapping("/{id}")
  public User patchUser(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
    // Note: Assuming a new service method is needed or using update with a wrapper
    return userService.update(id,
        new UserDto(null, null, null, request.newUsername(), request.newEmail(), null, null));
  }

  // 심화 요구사항
  @GetMapping("/findAll")
  public ResponseEntity<List<UserDto>> findAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }

  // Update online status

  @PatchMapping("/{id}/online")
  public User updateOnlineStatus(
      @PathVariable UUID id,
      @RequestBody OnlineStatusDto onlineStatusDto
  ) {
    return userService.updateOnlineStatus(id, onlineStatusDto.getOnline());
  }

  // Logging in
  @PostMapping("/login")
  public User login(@RequestBody LoginRequestDto loginDto) {
    return userService.login(loginDto.getEmail(), loginDto.getPassword());
  }


}



