package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import org.springframework.security.access.prepost.PreAuthorize;

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

  @PreAuthorize("hasRole('ADMIN')")
  UserResponse updateRole(UserRoleUpdateRequest request);

  void delete(UUID userId);
}