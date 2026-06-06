package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {

  UserResponse create(UserCreateRequest request,
      Optional<BinaryContentCreateRequest> binaryContentCreateRequest);

  UserResponse findById(UUID id);

  List<UserResponse> findAll();

  UserResponse update(UUID id, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> binaryContentCreateRequest);

  void delete(UUID userId);
}
