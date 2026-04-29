package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {
  private final UserService userService;
  private final UserStatusService userStatusService;

  @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
  public ResponseEntity<User> create(
      @RequestPart UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    try {
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest =
          BinaryContentRequestConverter.convert(profile);
      User user = userService.create(userCreateRequest, optionalProfileCreateRequest);
      return ResponseEntity.status(HttpStatus.CREATED).body(user);
    } catch (UserAlreadyException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = "multipart/form-data")
  public ResponseEntity<User> update(
      @PathVariable UUID userId,
      @RequestPart UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    try {
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest =
          BinaryContentRequestConverter.convert(profile);
      User user = userService.update(userId, userUpdateRequest, optionalProfileCreateRequest);
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

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> userDtos = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(userDtos);
  }

  @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
  ) {
    try {
      UserStatus status = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
      return ResponseEntity.status(HttpStatus.OK).body(status);
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}