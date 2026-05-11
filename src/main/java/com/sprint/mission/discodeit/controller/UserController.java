package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @RequestMapping(value = "create", method = RequestMethod.POST)
  public ResponseEntity<User> create(@ModelAttribute UserCreateRequest request) {
    User user = userService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(user);
  }

  @RequestMapping(value = "update", method = RequestMethod.PATCH)
  public ResponseEntity<UserDto> update(@ModelAttribute UserUpdateRequest request) {
    UserDto user = userService.update(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }

  @RequestMapping(value = "delete", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@RequestParam UUID id) {
    userService.delete(id);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(null);
  }

  @RequestMapping(value = "findAll", method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  @RequestMapping(value = "userStatusUpdate", method = RequestMethod.PATCH)
  public ResponseEntity<UserStatus> userStatusUpdate(@RequestParam UUID id) {
    UserStatus userStatus = userStatusService.updateByUserId(id);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userStatus);
  }

}
