package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

  //    User create(String newUsername, String newEmail, String newPassword);
  User create(UserCreateRequest request);

  UserDto find(UUID userId);

  List<UserDto> findAll();

  UserDto update(UserUpdateRequest request);

  void delete(UUID userId);
  // 이거로 전부 수정 예정
}
