package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest);

  UserDto findDetailById(UUID userId);

  List<UserDto> findAllWithFetch();

  UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest);

  UserDto updateRole(UserRoleUpdateRequest userRoleUpdateRequest);

  void delete(UUID userId);
}