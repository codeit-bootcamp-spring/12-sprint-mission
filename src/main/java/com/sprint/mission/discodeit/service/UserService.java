package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
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

  UserResponse update(UUID userId, UserUpdateRequest request,
                      Optional<BinaryContentCreateRequest> binaryContentCreateRequest);

  UserResponse updateRole(UserRoleUpdateRequest request);   // ← 선언만

  void delete(UUID userId);
}